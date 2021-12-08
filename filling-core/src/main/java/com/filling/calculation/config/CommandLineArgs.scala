package com.filling.calculation.config

case class CommandLineArgs(deployMode: String = "client",
                           configFile: String = "application.conf",
                           testConfig: Boolean = false)
