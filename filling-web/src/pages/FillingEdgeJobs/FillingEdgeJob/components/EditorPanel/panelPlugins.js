import _ from 'lodash';
import $ from 'jquery';

import BaseNode from '../EditorGraph/node';

import './index.less';

class panelPlugins {
  constructor() {
    // 渲染的一次的img数组（包括内置的和用户注册的）
    this.imgData = [];
    // 用户注册内容的数组
    this.userImgData = [];
    // 绑定过的canvas，用于防止重复绑定
    this.addCanvas = [];
  }

  guid = () => {
    function S4() {
      return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    return (S4() + S4() + "-" + S4());
  }

  addNode = (canvas, node) => {
    canvas.addNode(node);
  }

  register = (registerArray, callback) => {
    if (!_.isArray(registerArray)) {
      console.warn('register数据必须是数组', registerArray);
      return;
    }

    for (let registerData of registerArray) {
      if (!registerData.root) {
        console.warn('register数据root字段不存在=>', registerData);
        break;
      }
      if (!registerData.canvas) {
        console.warn('register数据canvas字段不存在=>', registerData);
        break;
      }

      if (registerData.data) {
        for (let item of registerData.data) {
          // item.width = item.width || 36;
          // item.height = item.height || 36;
          this.imgData.push(item);
          this.userImgData.push(item);
        }
      }

      for (let item of this.imgData) {
        let nodeItem = $('<div class="panel-node-dnd" drag></div>')
          .css('height', '90px');

        let img = new Image();
        // let img = document.createElement('svg');
        // img.src = './images/Elasticsearch.svg';
        img.src = item.icon;


        let jqImg = $(img).addClass('panel-img');
        jqImg.on('dragstart', (e) => {
          e.originalEvent.dataTransfer.setData('id', item.name + '-' + this.guid());
          e.originalEvent.dataTransfer.setData('originId', item.name);
          // e.originalEvent.dataTransfer.setData('data', JSON.stringify(item.data));
          e.originalEvent.dataTransfer.setData('pluginType', item['type']);
          e.originalEvent.dataTransfer.setData('pluginName', item['name']);
          e.originalEvent.dataTransfer.setData('label', item['label']);

          e.originalEvent.dataTransfer.setData('configGroupDefinition', JSON.stringify(item['configGroupDefinition']));
          e.originalEvent.dataTransfer.setData('configDefinitions', JSON.stringify(item['configDefinitions']));
          e.originalEvent.dataTransfer.setData('library', item['library']);
          e.originalEvent.dataTransfer.setData('stageName', item['name']);
          e.originalEvent.dataTransfer.setData('stageVersion', item['version']);

          e.originalEvent.dataTransfer.setData('endpoints', JSON.stringify(item['endpoints']));
          e.originalEvent.dataTransfer.setData('content', JSON.stringify(item['content']));
          e.originalEvent.dataTransfer.setDragImage(img, 0, 0);

          e.originalEvent.dataTransfer.setData('icon', item.icon);
        })

        nodeItem.append(img);
        nodeItem.append(`<div class='panel-lab'> ${item['label']} </div>`);
        $(registerData.root).append(nodeItem);
      }

      if (!this.addCanvas.includes(registerData.canvas.root)) {
        this.addCanvas.push(registerData.canvas.root);

        $(registerData.canvas.root).on('dragover', (e) => {
          e.preventDefault();
        });

        $(registerData.canvas.root).on('drop', (e) => {
          let { clientX, clientY } = e;
          let coordinates = registerData.canvas.terminal2canvas([clientX, clientY]);
          let id = e.originalEvent.dataTransfer.getData('id');
          // let data = JSON.parse(e.originalEvent.dataTransfer.getData('data'));

          let endpoints = JSON.parse(e.originalEvent.dataTransfer.getData('endpoints'));
          let PluginType = e.originalEvent.dataTransfer.getData('pluginType');
          let pluginName = e.originalEvent.dataTransfer.getData('pluginName');

          const configDefinitions = e.originalEvent.dataTransfer.getData('configDefinitions');
          const configGroupDefinition = e.originalEvent.dataTransfer.getData('configGroupDefinition');

          const library = e.originalEvent.dataTransfer.getData('library');
          const stageName = e.originalEvent.dataTransfer.getData('stageName');
          const stageVersion = e.originalEvent.dataTransfer.getData('stageVersion');

          let label = e.originalEvent.dataTransfer.getData('label');
          let icon = e.originalEvent.dataTransfer.getData('icon');

          let node = {
            id,
            left: coordinates[0],
            top: coordinates[1],
            Class: BaseNode,
            pluginName: pluginName,
            // data: data,
            PluginType: PluginType,
            endpoints: endpoints,
            icon,
            label,
            configDefinitions,
            configGroupDefinition,
            library,
            stageName,
            stageVersion
          }

          this.addNode(registerData.canvas, node);

        });
      }

      this.imgData = [];

    };

    if (_.isFunction(callback)) {
      callback();
    }

  }

  removeNode = () => {
    this.imgData = [];
    this.userImgData = [];
    this.addCanvas = [];
  }

}

let panelPluginsInstance = new panelPlugins();
panelPluginsInstance.PanelNode = BaseNode;

export { panelPluginsInstance };