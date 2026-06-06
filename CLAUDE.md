# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What Filling is

Filling is a drag-and-drop, graphical big-data processing tool built on **Apache Flink 1.14.4**. Users build data pipelines visually in a web UI; the backend compiles each pipeline into a JSON "job" and submits it to a Flink cluster. A pipeline is always `Source -> Transform(s) -> Sink`, wired together by named tables (`result_table_name` / `source_table_name`). Design is inspired by Waterdrop/SeaTunnel.

## Repository layout (Maven multi-module, parent `pom.xml`)

Three modules, built in order `filling-web`, `filling-service`, `filling-core`:

- **`filling-core`** — the Flink execution engine. Pure Java, no Spring. Packaged as a fat JAR that is uploaded to and run by the Flink cluster. This is where all data-processing logic lives (sources, transforms, sinks).
- **`filling-service`** — JHipster 7.1.0 / Spring Boot 2.4.7 backend. REST API, persistence (Liquibase + JPA), JWT auth, and the client that submits jobs to Flink. Main class `com.filling.FillingApp`.
- **`filling-web`** — Ant Design Pro + UmiJS (React) frontend. Contains the visual DAG editor (`src/pages/EditorFlow`). Built by `filling-web/pom.xml` via `frontend-maven-plugin` and bundled into the service.

> Note: `filling-service` is itself a full JHipster app and also embeds its own JHipster webapp build (`src/main/webapp`) separate from the `filling-web` module. The `filling-web` module is the primary product UI.

## Core engine architecture (`filling-core`)

Entry point: `com.filling.calculation.Filling#entryPoint(jsonObject, Engine, RunModel)`. Flow:

1. `ConfigBuilder` parses the job JSON and instantiates plugins by type (`PluginType.SOURCE/TRANSFORM/SINK`) via reflection.
2. `ConfigBuilder.createExecution()` returns a `FlinkStreamExecution`.
3. `Execution.start(sources, transforms, sinks, runModel)` chains them: each source produces a Flink `DataStream` registered under its `result_table_name`; each transform reads its `source_table_name` (or the previous stream) and re-registers output; sinks consume the final stream.

Key abstractions:

- **Plugin contract** — `com.filling.calculation.plugin.Plugin<T>`: every plugin implements `setConfig`, `getConfig`, `checkConfig` (returns `CheckResult`), and `prepare(env)`. Constants `RESULT_TABLE_NAME` / `SOURCE_TABLE_NAME` define table wiring.
- **Plugin APIs** — `apis/BaseSource`, `apis/BaseTransform`, `apis/BaseSink`. Flink implementations extend the `flink/BaseFlinkSource`, `BaseFlinkTransform`, `BaseFlinkSink` base classes and live under `plugin/base/flink/{source,transform,sink}`.
- **`Engine`** enum supports `FLINK` (active) and `SPARK`/`NULL` (placeholders). `RunModel` is `RUN` or `DEBUG` — DEBUG captures sample output to log files instead of running live.

To **add a new plugin**: create a class under the matching `plugin/base/flink/<type>` package extending the relevant `BaseFlink*` class, implement `checkConfig`/`prepare` and the stream method, then expose it in the web UI plugin list. Existing transforms (e.g. `Sql`, `DataJavascript`, `FieldRename`, `DataJoin`, `DataAggregates`) are good templates.

## Service ↔ cluster integration (`filling-service`)

- `FillingJobsService` is the job lifecycle hub: `start` submits, `stop` cancels, `plan` validates, `debugFillingJob` runs a job locally in DEBUG mode by shelling out to `java -cp ... com.filling.calculation.Filling <base64-json> debug`.
- Submission goes through the `client.ClusterClient` interface; the active implementation is `client.standalone.StandaloneClusterClient`, which uploads the `filling-core` JAR to Flink's REST API and POSTs `/jars/{id}/run` with the job JSON base64-encoded as `programArgs`.
- Flink connection is configured via `application.flink.*` properties (`url`, `jar`, `model`, `debug-lib-dir`), bound to `config/ApplicationProperties`. Per-environment values live in `src/main/resources/config/application-{dev,docker,prod}.yml`.
- Job pipelines are stored as the `jobText` JSON column on the `FillingJobs` entity; `toJobString()` produces the payload sent to the engine. `FillingEdgeJobs`/`FillingEdgeNodes` model edge (远程节点) deployments.

## Build & run

Requires **JDK 11** and Node. Use the wrapper `./mvnw` (Maven) at the repo root.

```bash
# Full build of all three modules (produces the service JAR + core fat JAR)
./mvnw -B package                 # add -DskipTests to skip tests (as CI does)

# Build a single module (-am also builds its dependencies)
./mvnw -pl filling-core -am package
./mvnw -pl filling-service -am package

# Run the backend (JHipster dev server, port 8080)
cd filling-service && ../mvnw    # or: ./mvnw -pl filling-service spring-boot:run
```

Frontend (`filling-web`, UmiJS — port from UMI):

```bash
cd filling-web
npm install
npm run start:dev   # dev server, mock off  (npm start = with UMI mock)
npm run build       # production bundle into dist/
npm run lint        # eslint + stylelint + prettier
npm run lint:fix    # autofix
npm test            # umi/jest tests
```

## Tests (`filling-core`)

Tests use JUnit 4 and run a real Flink pipeline from a JSON config file under `src/test/resources/flink/`. Example pattern (`source/stream/DataGenTest`): read a config JSON, call `Filling.entryPoint(str, Engine.FLINK)`.

```bash
# Run all core tests
./mvnw -pl filling-core test

# Run a single test class or method
./mvnw -pl filling-core test -Dtest=DataGenTest
./mvnw -pl filling-core test -Dtest=DataGenTest#testGenDataefile
```

> Caveat: many existing core tests hardcode an absolute `rootPath` (e.g. `/Users/jiangzihan/filling/...`) for loading resource configs. These will not pass as-is in other environments — when adding tests, load resources from the classpath instead of a hardcoded path.

## Deployment

- `Dockerfile` (repo root) builds the runtime image: it copies the service JAR (`app.war`), the `filling-core` fat JAR, and `flink-jars/`, running as the non-root `filling` user via `entrypoint.sh`. Active Spring profile defaults to `docker`.
- `app.yml` is a docker-compose stack (Filling + Flink jobmanager + 4 taskmanagers on `flink:1.14.3`).
- CI: `.github/workflows/maven-publish.yml` builds with `mvn package -DskipTests` on push to `main`/`releases/**` and pushes a Docker image to Docker Hub (`zihjiang/filling`).

## Conventions

- **Java**: target 11. `filling-core` uses FastJSON (`com.alibaba.fastjson.JSONObject`) for all job/config JSON, Lombok, and OkHttp. `filling-service` follows standard JHipster layering: `web/rest` (controllers) → `service` (+ `dto`/`mapper` via MapStruct) → `repository` → `domain`. Schema changes go through Liquibase changelogs under `resources/config/liquibase`.
- **Frontend**: commits use commitizen (`.cz-config.js`); lint-staged runs eslint/stylelint/prettier on commit. Locale strings live under `src/locales` (default `zh-CN`).
- **Language**: code comments, commit messages, and docs are frequently in Chinese — match the surrounding style.
- Full documentation: https://zihjiang.github.io/filling-book/
