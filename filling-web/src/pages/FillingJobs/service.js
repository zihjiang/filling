// @ts-ignore

/* eslint-disable */
import request from '@/utils/request';

/** 获取列表 GET /api/filling-jobs */

export async function fillingJob(id, options) {
  return request('/api/filling-jobs/' + id, {
    method: 'GET',
    ...(options || {}),
  });
}


/** 获取列表 GET /api/filling-jobs */

export async function fillingJobs(params, options) {
  return request('/api/filling-jobs', {
    method: 'GET',
    params: { ...params },
    ...(options || {}),
  });
}
/** 新建任务 PUT /api/filling-jobs */

export async function updateFillingJobs(options) {
  return request('/api/filling-jobs', {
    method: 'PUT',
    ...(options || {}),
  });
}
/** 新建任务 POST /api/filling-jobs */

export async function addFillingJobs(options) {
  return request('/api/filling-jobs', {
    method: 'POST',
    ...(options || {}),
  });
}
/** 删除任务 DELETE /api/filling-jobs */

export async function removeFillingJobs(id) {
  console.log(id);
  return request('/api/filling-jobs/' + id, {
    method: 'DELETE'
  });
}

/** 修改部分任务 PATCH /api/filling-jobs */

export async function patchFillingJobs(id, options) {
  options.data.id = id;
  return request('/api/filling-jobs/' + id, {
    method: 'PATCH',
    ...(options || {})
  });
}

/** 启动一个任务 START /api/filling-jobs */

export async function startFillingJobs(id) {
  return request('/api/filling-jobs/' + id + '/start', {
    method: 'GET'
  });
}

/** 停止一个任务 STOP /api/filling-jobs */

export async function stopFillingJobs(id) {
  return request('/api/filling-jobs/' + id + '/stop', {
    method: 'GET'
  });
}

/** 检查一个任务是否能正常工作 plan /api/filling-jobs */

export async function planFillingJobs(id) {
  return request('/api/filling-jobs/' + id + '/plan', {
    method: 'GET'
  });
}
  /** 导出任务 export /filling-job/export/{id} */

  export async function exportFillingJob(id) {
    return request('/api/filling-job/' + id + '/export', {
      method: 'GET'
    });
  }

  /** 入任务 export /filling-job/import */

  export async function importFillingJob(options) {
    return request('/api/filling-job/import', {
      method: 'POST',
      ...(options || {}),
    });
  }