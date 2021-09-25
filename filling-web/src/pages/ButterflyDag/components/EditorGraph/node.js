
import {Node} from 'butterfly-dag';
import $ from 'jquery';
class BaseNode extends Node {
  constructor(opts) {
    super(opts);
    this.titleBox = null;
    this.state = {
    }

  }
  mounted() {
    
  }

  
  draw = (data) => {
    let container = $('<div class= "test-base-node"></div>')
      .css('top', data.top)
      .css('left', data.left)
      .css('width', 140)
      .css('height', 90);
    switch (this.options.PluginType) {
      case 'source' :
        this.logEventDom = $('<div class="butterflie-circle-endpoint system-green-point"></div>');
        break;
      case 'transform':
        this.logEventDom = $('<div class="custom-green-rectangle-point system-green-point"></div>');
        break;
      case 'sink':
        this.widEndpointDom = $(`<div class="custom-green-circle-point widgest-point_1"></div>`);
        break;
      default:
    }

    // if (this.logEventDom) {
    //   container.append(this.logEventDom);
    // }

    // if (this.widEndpointDom) {
    //   container.append(this.widEndpointDom);
    // }

    container.append(`<span class='text'>${data.options.text}</span>`);
    container.append(`<img class='image' src=${ data.options.content}></img>`);


    $(container).on('dblclick', () => {

      // paramsFrom
      window.selectNode = data;
      window.selectNodeOrEdge = data;
      $("#ParamsFrom > div").trigger('click');
    });

    $(container).on('click', () => {
      // paramsFrom
      window.selectNode = data;
      window.selectNodeOrEdge = data;
      $('.test-base-node').removeClass('test-base-node_selected');
      $(container).addClass('test-base-node_selected');
    });

    return container[0];
  }

  unFocus = () => {

  }
}
export default BaseNode;
