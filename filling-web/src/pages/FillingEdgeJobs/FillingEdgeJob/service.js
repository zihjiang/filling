// @ts-ignore

/* eslint-disable */
import request from '@/utils/request';

/** 获取列表 GET /api/filling-edge-jobs */

export async function fillingEdgeJobById(id, options) {
  return request('/api/filling-edge-jobs/' + id, {
    method: 'GET',
    ...(options || {}),
  });
}


/** 获取列表 GET /api/filling-edge-jobs */

export async function fillingEdgeJob(params, options) {
  return request('/api/filling-edge-jobs', {
    method: 'GET',
    params: { ...params },
    ...(options || {}),
  });
}
/** 新建任务 PUT /api/filling-edge-jobs */

export async function updateFillingEdgeJob(options) {
  return request('/api/filling-edge-jobs', {
    method: 'PUT',
    ...(options || {}),
  });
}
/** 新建任务 POST /api/filling-edge-jobs */

export async function addFillingEdgeJob(options) {
  return request('/api/filling-edge-jobs', {
    method: 'POST',
    ...(options || {}),
  });
}
/** 删除任务 DELETE /api/filling-edge-jobs */

export async function removeFillingEdgeJob(id) {
  console.log(id);
  return request('/api/filling-edge-jobs/' + id, {
    method: 'DELETE'
  });
}

/** 修改部分任务 PATCH /api/filling-edge-jobs */

export async function patchFillingEdgeJob(id, options) {
  options.data.id = id;
  return request('/api/filling-edge-jobs/' + id, {
    method: 'PATCH',
    ...(options || {})
  });
}

/** 预览任务 PATCH /api/filling-edge-jobs/{id}/preview */

export async function previewFillingEdgeJob(id, options) {
  options.data.id = id;
  return request('/api/filling-edge-jobs/' + id + '/preview', {
    method: 'POST',
    ...(options || {})
  });
}
// 根据nodeId查找job
export async function findByFillingEdgeNodesId(id) {
  return request('/api/filling-edge-jobs/by-node-id/' + id, {
    method: 'GET'
  });
}