/*
 * @Author: pengyu
 * @Date: 2020-12-08 23:22:30
 * @LastEditTime: 2020-12-27 16:37:10
 * @LastEditors: Please set LastEditors
 * @Description: 房价牌管理-接口
 * @FilePath: \RuoYi-Vue\ruoyi-ui\src\api\priceTag\index.js
 */
import request from '@/utils/request'

export function downloadFile(params){
  return request({
    url: '/common/download',
    method: 'get',
    params
  })
}

// 查询酒店信息
export function getHotelInfo() {
  return request({
    url: '/hotel/hotelInfo/query',
    method: 'get',
  })
}

// 保存酒店信息
export function saveHotelInfo(params) {
  return request({
    url: '/hotel/hotelInfo/save',
    method: 'post',
    params
  })
}

//上传酒店LOGO
export function uploadLogo(params, headers){
  return request({
    url: '/hotel/hotelInfo/logo',
    method: 'post',
    params,
    headers
  })
}

//删除酒店logo
export function deleteLogo(){
  return request({
    url: '/hotel/hotelInfo/deleteLogo',
    method: 'post'
  })
}

//删除酒店二维码
export function deleteQrCode(){
  return request({
    url: '/hotel/hotelInfo/deleteQrCode',
    method: 'post'
  })
}

//查询房价类型
export function getPriceType() {
  return request({
    url: '/hotel/priceType/query',
    method: 'get'
  })
}

//新增房价类别
export function addPriceType(params) {
  return request({
    url: '/hotel/priceType/add',
    method: 'post',
    params
  })
}

//修改房价类别
export function editPriceType(params) {
  return request({
    url: '/hotel/priceType/edit',
    method: 'put',
    params
  })
}

//删除房价类别
export function deletePriceType(params) {
  return request({
    url: '/hotel/priceType/delete',
    method: 'delete',
    params
  })
}

//显示隐藏房价类别
export function switchPriceType(params) {
  return request({
    url: '/hotel/priceType/switched',
    method: 'post',
    params
  })
}

//查询房间价格列表
export function getRoomPriceList() {
  return request({
    url: '/hotel/roomPrice/query',
    method: 'get'
  })
}

//新增房间价格
export function addRoomPrice(params) {
  return request({
    url: '/hotel/roomPrice/add',
    method: 'post',
    params
  })
}

//修改房间价格
export function editRoomPrice(params) {
  return request({
    url: '/hotel/roomPrice/edit',
    method: 'put',
    params
  })
}

//删除房间价格
export function deleteRoomPrice(params) {
  return request({
    url: '/hotel/roomPrice/delete',
    method: 'delete',
    params
  })
}

//获取房间图片信息
export function getRoomPicture() {
  return request({
    url: '/hotel/roomPicture/query',
    method: 'get'
  })
}

//删除房间图片信息
export function deleteRoomPicture(params) {
  return request({
    url: '/hotel/roomPicture/delete',
    method: 'delete',
    params
  })
}

//替换房间图片信息
export function replaceRoomPicture(params) {
  return request({
    url: '/hotel/roomPicture/replace',
    method: 'delete',
    params
  })
}

//停用启用图片
export function enableRoomPicture(params) {
  return request({
    url: '/hotel/roomPicture/switched',
    method: 'post',
    params
  })
}

//修改图片名称序号
export function editRoomPicture(params) {
  return request({
    url: '/hotel/roomPicture/edit',
    method: 'post',
    params
  })
}

//房间图片排序
export function orderRoomPicture(params) {
  return request({
    url: '/hotel/roomPicture/order',
    method: 'post',
    params
  })
}


