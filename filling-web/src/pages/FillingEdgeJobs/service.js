// @ts-ignore

/* eslint-disable */
import request from '@/utils/request';

/** 获取列表 GET /api/filling-edge-nodes */

export async function fillingEdgeNodeById(id, options) {
  return request('/api/filling-edge-nodes/' + id, {
    method: 'GET',
    ...(options || {}),
  });
}


/** 获取列表 GET /api/filling-edge-nodes */

export async function fillingEdgeNodes(params, options) {
  return request('/api/filling-edge-nodes', {
    method: 'GET',
    params: { ...params },
    ...(options || {}),
  });
}
/** 新建任务 PUT /api/filling-edge-nodes */

export async function updateFillingEdgeNode(options) {
  return request('/api/filling-edge-nodes', {
    method: 'PUT',
    ...(options || {}),
  });
}
/** 新建任务 POST /api/filling-edge-nodes */

export async function addFillingEdgeNode(options) {
  return request('/api/filling-edge-nodes', {
    method: 'POST',
    ...(options || {}),
  });
}
/** 删除任务 DELETE /api/filling-edge-nodes */

export async function removeFillingEdgeNode(id) {
  console.log(id);
  return request('/api/filling-edge-nodes/' + id, {
    method: 'DELETE'
  });
}

/** 修改部分任务 PATCH /api/filling-edge-nodes */

export async function patchFillingEdgeNode(id, options) {
  options.data.id = id;
  return request('/api/filling-edge-nodes/' + id, {
    method: 'PATCH',
    ...(options || {})
  });
}