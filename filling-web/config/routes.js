export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user',
        routes: [
          {
            name: 'login',
            path: '/user/login',
            component: './user/Login',
          },
        ],
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/welcome',
    name: 'welcome',
    icon: 'smile',
    component: './Welcome',
  },
  {
    path: '/admin',
    name: 'admin',
    icon: 'crown',
    access: 'canAdmin',
    component: './Admin',
    routes: [
      {
        path: '/admin/sub-page',
        name: 'sub-page',
        icon: 'smile',
        component: './Welcome',
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    name: '流式计算',
    icon: 'smile',
    path: '/fillingjobs',
    component: './FillingJobs',
  },
  {
    name: '编辑任务',
    icon: 'smile',
    path: '/butterfly-dag/:id?',
    hideInMenu: true,
    component: './ButterflyDag',
  },
  {
    name: '边缘节点',
    icon: 'smile',
    path: '/fillingedgeJobs',
    component: './FillingEdgeJobs',
  },
  {
    name: '编辑边缘任务',
    icon: 'smile',
    path: '/FillingEdgeJobs/:nodeId/FillingEdgeJob/:id?',
    hideInMenu: true,
    component: './FillingEdgeJobs/FillingEdgeJob',
  },
  {
    component: './404',
  },
];
