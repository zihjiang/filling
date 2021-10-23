/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import { extend } from 'umi-request';
import { notification } from 'antd';
import jwt_decode from 'jwt-decode'
const codeMessage = {
    200: '服务器成功返回请求的数据。',
    201: '新建或修改数据成功。',
    202: '一个请求已经进入后台排队（异步任务）。',
    204: '删除数据成功。',
    400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
    401: '用户没有权限（令牌、用户名、密码错误）。',
    403: '用户得到授权，但是访问是被禁止的。',
    404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
    406: '请求的格式不可得。',
    410: '请求的资源被永久删除，且不会再得到的。',
    422: '当创建一个对象时，发生一个验证错误。',
    500: '服务器发生错误，请检查服务器。',
    502: '网关错误。',
    503: '服务不可用，服务器暂时过载或维护。',
    504: '网关超时。',
};
/**
 * 异常处理程序
 */
const getUserToken = () => {
    return localStorage.getItem("id_token");
}
const removeUserToken = () => {
    localStorage.removeItem("id_token");
}
const errorHandler = error => {
    const { response } = error;

    if (response && response.status) {
        const errorText = codeMessage[response.status] || response.statusText;
        const { status, url } = response;
        notification.error({
            message: `请求错误 ${status}: ${url}`,
            description: errorText,
        });
    } else if (!response) {
        notification.error({
            description: '您的网络发生异常，无法连接服务器',
            message: '网络异常',
        });
    }

    return response;
};
/**
 * 配置request请求时的默认参数
 */

const request = extend({
    errorHandler,
    // 默认错误处理
    credentials: 'include', // 默认请求是否带上cookie
});

let guoqi = true;
let ispending = false;
request.interceptors.request.use(async (url, options) => {

    const token = getUserToken();
    if (token) {
        //如果有token 就走token逻辑
        const headers = {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        };
        let decodeToken = jwt_decode(token);
        const { exp, refreshTime } = decodeToken;
        const maxTime = exp * 1000 + refreshTime;
        const nowTime = new Date().getTime();
        console.log(parseInt((exp * 1000 - nowTime) / 1000) + '秒后重新登录')
        if (nowTime >= maxTime) {
            //token过期，而且延期token也过去了，那么，清空你的数据，直接返回登录，不允许操作了
            console.log('超时了')
            location.href = '/user/login'
            return;
        }
        console.log(parseInt((exp * 1000 - nowTime) / 1000) + '秒后过期')
        if (nowTime >= exp * 1000) {
            //只是过期了，那就去拿新的token

            console.log("token 过期");
            removeUserToken();
            if (ispending) {
                //如果正在发送中，此请求就等一会吧，生成一个Promise 等新token返回的时候，我再resolve
            } else {
                //如果没发送，立刻改为发送状态
                ispending = true;

            }
            return new Promise((resolve, reject) => {
                _cacheRequest.push(() => {
                    resolve()
                })
            });
        }
        console.log(options);
        return ({
            url: url,
            options: { ...options, headers: headers },
        });
    } else if(location.pathname != '/user/login') {
        console.log("登陆超时");
        notification.error({
            message: `登录超时, 请重新登陆`,
            description: errorText,
        });
        location.href = "/user/login";
    }
    return ({
        url: url,
        options: options,
    });
})
//第二个拦截器，为什么会存在第二个拦截器呢？就是因为第一个拦截器有可能返回Promise,那么Promise由第二个拦截器处理吧。之前因为这个问题跟umi提了issues。原来是我没搞明白。。。
request.interceptors.request.use(async (url, options) => {
    const token = getUserToken();
    if (token) {
        //如果有token 就走token逻辑
        const headers = {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        };
        return ({
            url: url,
            options: { ...options, headers: headers },
        });
    }
    return ({
        url: url,
        options: options,
    });



})
export default request;