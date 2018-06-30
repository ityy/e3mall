<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <ul id="contentCategory" class="easyui-tree">
    </ul>
</div>

<%--右键点击树节点时的菜单--%>
<%--绑定鼠标点击事件 调用menuHandler--%>
<div id="contentCategoryMenu" class="easyui-menu" style="width:120px;" data-options="onClick:menuHandler">
    <div data-options="iconCls:'icon-add',name:'add'">添加</div>
    <div data-options="iconCls:'icon-remove',name:'rename'">重命名</div>
    <div class="menu-sep"></div>
    <div data-options="iconCls:'icon-remove',name:'delete'">删除</div>
</div>
<script type="text/javascript">
    $(function () {
        $("#contentCategory").tree({
            url: '/content/category/list',
            animate: true,
            method: "GET",
            //右键点击事件 e就是事件 这里仅显示菜单
            onContextMenu: function (e, node) {
                e.preventDefault();
                $(this).tree('select', node.target);
                $('#contentCategoryMenu').menu('show', {
                    //e中含有鼠标的坐标, 这里指定右键菜单显示的坐标为鼠标的坐标
                    left: e.pageX,
                    top: e.pageY
                });
            },


            //编辑后触发的事件 用于向数据库存储节点
            onAfterEdit: function (node) {
                var _tree = $(this);
                if (node.id == 0) {
                    //当id为0 则编辑的是新增的节点 使用添加方法
                    $.post("/content/category/create", {parentId: node.parentId, name: node.text}, function (data) {
                        if (data.status == 200) {
                            //当添加成功后 更新正显示的此节点 使其ID不为0 取消其为新增节点的状态
                            _tree.tree("update", {
                                target: node.target,
                                //中间的data是E3result中的属性
                                id: data.data.id
                            });
                        } else {
                            $.messager.alert('提示', '创建' + node.text + ' 分类失败!');
                        }
                    });
                } else {
                    //当id不为0 则编辑的是已存在的节点 使用更新方法
                    $.post("/content/category/update", {id: node.id, name: node.text});
                }
            }
        });
    });


    //点击右键菜单中的选项后的事件所调用的方法
    function menuHandler(item) {
        var tree = $("#contentCategory");
        var node = tree.tree("getSelected");
        //判断鼠标点击的是哪个方法 add|rename|delete
        if (item.name === "add") {
            tree.tree('append', {
                parent: (node ? node.target : null),
                data: [{
                    text: '新建分类',
                    id: 0,
                    parentId: node.id
                }]
            });
            //由于新加的节点id为0, 所以查找为0的节点 并选中 且进入编辑状态(即重命名)
            var _node = tree.tree('find', 0);
            tree.tree("select", _node.target).tree('beginEdit', _node.target);

        } else if (item.name === "rename") {
            tree.tree('beginEdit', node.target);
        } else if (item.name === "delete") {
            $.messager.confirm('确认', '确定删除名为 ' + node.text + ' 的分类吗？', function (r) {
                if (r) {
                    $.post("/content/category/delete", {id: node.id}, function (data) {
                        if (data.status == 200) {
                            //删除成功，更新树
                            tree.tree("remove", node.target);
                        } else {
                            $.messager.alert('提示', data.msg);
                        }
                    });
                }
            });
        }
    }
</script>