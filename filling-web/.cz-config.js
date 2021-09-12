'use strict';

module.exports = {

  types: [
    {
      value: ':construction: WIP',
      name: '💪  WIP:      Work in progress'
    },
    {
      value: ':sparkles: feat',
      name: '✨  feat:     A new feature'
    },
    {
      value: ':bug: fix',
      name: '🐛  fix:      A bug fix'
    },
    {
      value: ':hammer: refactor',
      name: '🔨  refactor: A code change that neither fixes a bug nor adds a feature'
    },
    {
      value: ':pencil: docs',
      name: '📝  docs:     Documentation only changes'
    },
    {
      value: ':white_check_mark: test',
      name: '✅  test:     Add missing tests or correcting existing tests'
    },
    {
      value: ':thought_balloon: chore',
      name: '🗯  chore:    Changes that don\'t modify src or test files. Such as updating build tasks, package manager'
    },
    {
      value: ':lipstick: ui',
      name: '💄 Updating the UI and style files.',
    },
    {
      value: ':art: style',
      name:
        '🎨 Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)',
    },
    {
      value: 'revert',
      name: '⏪  revert:   Revert to a commit'
    },
    {
      value: ':package: dep_up',
      name: '📦 Updating compiled files or packages.',
    },
    {
      value: ':green_heart: fixci',
      name: '💚 Fixing CI Build.',
    },
    {
      value: ':truck: mv',
      name: '🚚 Moving or renaming files.',
    },
    {
      value: ':fire: prune',
      name: '🔥 Removing code or files.',
    },
    {
      value: ':bookmark: release',
      name: '🔖 Releasing / Version tags.',
    },
    {
      value: ':rocket: first release',
      name: '🚀 first releast!',
    }
  ],

  // scopes: [{ name: 'accounts' }, { name: 'admin' }, { name: 'exampleScope' }, { name: 'changeMe' }],

  // allowTicketNumber: false,
  // isTicketNumberRequired: false,
  // ticketNumberPrefix: 'TICKET-',
  // ticketNumberRegExp: '\\d{1,5}',

  messages: {
    type: '选择一种你的提交类型:',
    scope: '选择修改涉及范围 (可选):',
    // used if allowCustomScopes is true
    customScope: '请输入本次改动的范围（如：功能、模块等）:',
    subject: '简短说明:\n',
    body: '详细说明，使用"|"分隔开可以换行(可选)：\n',
    breaking: '非兼容性，破坏性变化说明 (可选):\n',
    footer: '关联关闭的issue，例如：#31, #34(可选):\n',
    confirmCommit: '确定提交说明?'
  },

  allowCustomScopes: true,
  allowBreakingChanges: ["feat", "fix"],  // 仅在feat、fix时填写破坏性更改
  subjectLimit: 100, // limit subject length
  breaklineChar: '|',  // 设置body和footer中的换行符
};

