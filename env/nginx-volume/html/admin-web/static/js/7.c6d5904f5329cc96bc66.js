webpackJsonp([7],{Hqjd:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=a("Xxa5"),s=a.n(r),n=a("Dd8w"),o=a.n(n),l=a("exGp"),i=a.n(l),c=a("mvHQ"),u=a.n(c),m={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("section",{staticClass:"filter"},[a("div",{staticClass:"filter-container"},[a("el-form",{ref:"form",attrs:{inline:!0}},[a("el-form-item",{attrs:{label:"频道名称："}},[a("el-input",{staticClass:"filter-item",attrs:{placeholder:"请输入频道名称",clearable:""},on:{change:e.queryData},model:{value:e.name,callback:function(t){e.name=t},expression:"name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"账号状态："}},[a("el-select",{attrs:{placeholder:"请选择状态",clearable:""},on:{change:e.changeState},model:{value:e.selectState,callback:function(t){e.selectState=t},expression:"selectState"}},e._l(e.stateList,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1)],1)],1)],1),e._v(" "),a("el-button",{attrs:{type:"success",icon:"el-icon-circle-plus-outline"},on:{click:e.addData}},[e._v("新建")])],1)},staticRenderFns:[]},d=a("VU/8")({props:["changeParam","addData"],data:function(){return{stateList:[{label:"全部",value:""},{label:"启动",value:1},{label:"禁用",value:0}],name:"",selectState:""}},methods:{queryData:function(){var e={name:""};this.name&&(e.name=this.name),this.changeParam(e)},changeState:function(){this.queryData()}}},m,!1,null,null,null).exports,f=a("xT6B"),p=a("vLgD"),h=a("2EN7");function g(e){return new p.a({url:h.i,method:"post",data:e})}function b(e){return new p.a({url:h.f+"/"+e,method:"get"})}var v={props:["list","table","pageSize","total","changePage","submitSuccess","editData"],data:function(){return{listPage:{currentPage:1}}},methods:{pageChange:function(e){this.changePage&&this.changePage({page:e})},dateFormat:function(e){return f.a.format13(e)},operateForEditor:function(e){this.editData(e)},updateChannel:function(e,t,a){var r=this;return i()(s.a.mark(function n(){var o,l;return s.a.wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return o={id:e,name:t,status:a},s.next=3,g(o);case 3:200===(l=s.sent).code?(r.dialogFormVisible=!1,r.submitSuccess(),r.$message({type:"success",message:"操作成功！"})):r.$message({type:"error",message:l.errorMessage});case 5:case"end":return s.stop()}},n,r)}))()},delChannel:function(e){var t=this;return i()(s.a.mark(function a(){var r;return s.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,b(e);case 2:200===(r=a.sent).code?(t.dialogFormVisible=!1,t.submitSuccess(),t.$message({type:"success",message:"删除成功！"})):t.$message({type:"error",message:r.errorMessage});case 4:case"end":return a.stop()}},a,t)}))()}}},_={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",[a("section",{staticClass:"result"},[a("el-table",{attrs:{data:e.list,"header-cell-style":{textAlign:"center",fontWeight:"600"},"cell-style":{textAlign:"center"},"highlight-current-row":""}},[a("el-table-column",{attrs:{label:"序号",type:"index",width:"50"}}),e._v(" "),a("el-table-column",{attrs:{label:"频道名称"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.name))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"描述"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.description))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"是否默认频道"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.isDefault?"是":"否")+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"状态"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v(e._s(1==t.row.status?"启动":"禁用"))]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"操作",width:"240"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{staticClass:"el-button--success-text",attrs:{type:"text"},on:{click:function(a){return e.operateForEditor(t.row)}}},[e._v("编辑")]),e._v(" "),a("el-button",{staticClass:"el-button--primary-text",attrs:{type:"text",disabled:t.row.status},on:{click:function(a){return e.updateChannel(t.row.id,t.row.name,!0)}}},[e._v("启用")]),e._v(" "),a("el-button",{staticClass:"el-button--danger-text",attrs:{type:"text",disabled:!t.row.status},on:{click:function(a){return e.updateChannel(t.row.id,t.row.name,!1)}}},[e._v("禁用")]),e._v(" "),a("el-button",{staticClass:"el-button--danger-text",attrs:{type:"text"},on:{click:function(a){return e.delChannel(t.row.id)}}},[e._v("删除")])]}}])})],1)],1),e._v(" "),a("el-pagination",{attrs:{layout:"total, sizes, prev, pager, next, jumper","current-page":e.listPage.currentPage,"page-size":e.pageSize,total:e.total},on:{"current-change":e.pageChange,"update:currentPage":function(t){return e.$set(e.listPage,"currentPage",t)},"update:current-page":function(t){return e.$set(e.listPage,"currentPage",t)}}})],1)},staticRenderFns:[]},x={name:"commn-editor",props:["title","submitSuccess"],data:function(){return{disable:!1,model:"add",dialogFormVisible:!1,formLabelWidth:"80px",form:{},rules:{},stateList:[{label:!0,value:"启动"},{label:!1,value:"禁用"}]}},methods:{add:function(){this.dialogFormVisible=!0,this.form={name:"",status:!1},this.model="add"},edit:function(e){this.dialogFormVisible=!0,this.form=e,this.model="edit"},submit:function(){var e,t=this;this.$refs.commForm.validate((e=i()(s.a.mark(function e(a){return s.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:if(!a){e.next=4;break}"add"===t.model?t.saveChannel():t.updateChannel(),e.next=5;break;case 4:return e.abrupt("return",!1);case 5:case"end":return e.stop()}},e,t)})),function(t){return e.apply(this,arguments)}))},saveChannel:function(){var e=this;return i()(s.a.mark(function t(){var a,r;return s.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return a={name:e.form.name,status:e.form.status,description:e.form.description,ord:e.form.ord},t.next=3,s=a,new p.a({url:h.h,method:"post",data:s});case 3:200===(r=t.sent).code?(e.dialogFormVisible=!1,e.submitSuccess(),e.$message({type:"success",message:"操作成功！"})):e.$message({type:"error",message:r.errorMessage});case 5:case"end":return t.stop()}var s},t,e)}))()},updateChannel:function(){var e=this;return i()(s.a.mark(function t(){var a,r;return s.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return a={id:e.form.id,name:e.form.name,status:e.form.status,description:e.form.description,ord:e.form.ord},t.next=3,g(a);case 3:200===(r=t.sent).code?(e.dialogFormVisible=!1,e.submitSuccess(),e.$message({type:"success",message:"操作成功！"})):e.$message({type:"error",message:r.errorMessage});case 5:case"end":return t.stop()}},t,e)}))()}}},w={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{ref:"dialog",attrs:{title:e.title,visible:e.dialogFormVisible,width:"442px"},on:{"update:visible":function(t){e.dialogFormVisible=t}}},[a("el-form",{ref:"commForm",attrs:{model:e.form,rules:e.rules,inline:!0}},[a("el-form-item",{attrs:{label:"频道名称："}},[a("el-input",{attrs:{autocomplete:"off",placeholder:"请输入频道名称"},model:{value:e.form.name,callback:function(t){e.$set(e.form,"name",t)},expression:"form.name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"频道描述："}},[a("el-input",{attrs:{autocomplete:"off",placeholder:"请输入频道描述"},model:{value:e.form.description,callback:function(t){e.$set(e.form,"description",t)},expression:"form.description"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"是否启动："}},[a("el-radio-group",{model:{value:e.form.status,callback:function(t){e.$set(e.form,"status",t)},expression:"form.status"}},e._l(e.stateList,function(t,r){return a("el-radio",{key:r,attrs:{label:t.label}},[e._v(e._s(t.value))])}),1)],1),e._v(" "),a("el-form-item",{attrs:{label:"排序方式："}},[a("el-input",{attrs:{autocomplete:"off",placeholder:"请输入排序方式",type:"number"},model:{value:e.form.ord,callback:function(t){e.$set(e.form,"ord",e._n(t))},expression:"form.ord"}})],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"warning"},on:{click:function(t){e.dialogFormVisible=!1}}},[e._v("取消")]),e._v(" "),a("el-button",{attrs:{type:"success"},on:{click:e.submit}},[e._v("确定")])],1)],1)},staticRenderFns:[]};var y={name:"ChannelManager",data:function(){return{params:{name:"",page:1,size:10},total:0,host:"",list:[],title:""}},mounted:function(){this.loadData()},components:{SearchTool:d,SearchResult:a("VU/8")(v,_,!1,null,null,null).exports,Editor:a("VU/8")(x,w,!1,function(e){a("a43h")},"data-v-6bd55048",null).exports},methods:{editData:function(e){this.title="编辑频道",this.$refs.editor.edit(JSON.parse(u()(e)))},addData:function(e){this.title="新增频道",this.$refs.editor.add()},submitSuccess:function(){this.loadData()},changeParam:function(e){this.params.page=1,this.params.name=e.name,this.loadData()},changePage:function(e){this.params.page=e.page,this.loadData()},loadData:function(){var e=this;return i()(s.a.mark(function t(){var a;return s.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,r=o()({},e.params),new p.a({url:h.g,method:"post",data:r});case 2:200===(a=t.sent).code?(e.list=a.data,e.host=a.host,e.total=a.total):e.$message({type:"error",message:a.errorMessage});case 4:case"end":return t.stop()}var r},t,e)}))()}}},k={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",[a("Editor",{ref:"editor",attrs:{title:e.title,table:this.params.name,submitSuccess:e.submitSuccess}}),e._v(" "),a("search-tool",{attrs:{changeParam:e.changeParam,addData:e.addData}}),e._v(" "),a("search-result",{ref:"mySearchResult",attrs:{list:e.list,total:e.total,table:this.params.name,editData:e.editData,changePage:e.changePage,submitSuccess:e.submitSuccess,pageSize:e.params.size}})],1)},staticRenderFns:[]},S=a("VU/8")(y,k,!1,null,null,null);t.default=S.exports},a43h:function(e,t){}});
//# sourceMappingURL=7.c6d5904f5329cc96bc66.js.map