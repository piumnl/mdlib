const TREE_ID = 'codeTree';

(function () {
    /**
     * init css
     */
    let initCss = function () {
        let isStandardMode = document.compatMode && document.compatMode === 'CSS1Compat';
        let body = isStandardMode ? document.documentElement : document.body;
        let height = body.clientHeight;
        $('.left').css('height', height);
        $('.right').css('height', height);
    };
    initCss();

    /**
     * init tree
     */
    let initTree = function () {
        let setting = {
            view: {
                // 搜索时高亮
                fontCss: function(treeId, treeNode) {
                    return (!!treeNode.highlight) ? {color: "#A60000", "font-weight": "bold"}
                                                  : {color: "#333", "font-weight": "normal"};
                }
            },
            data: {
                simpleData: {
                    enable: true
                }
            }
        };

        let zNodes = nodes;
        zNodes.target = 'content';

        // 对 tree 做一些操作
        let nodeAddTarget = function(nodes) {
            if (!nodes) {
                return;
            }

            for (let i = 0; i < nodes.length; i++) {
                if (nodes[i].children) {
                    nodeAddTarget(nodes[i].children);
                    nodes[i].url = undefined;
                    nodes[i].target = undefined;
                } else {
                    let extIndex = nodes[i].url.indexOf('.');
                    if (extIndex !== -1) {
                        nodes[i].name = nodes[i].name.substr(0, nodes[i].name.indexOf('.'));
                        nodes[i].url = nodes[i].url.substr(0, extIndex) + '.html';
                        nodes[i].target = nodes[i].target || 'content';
                    } else {
                        nodes[i].url = undefined;
                        nodes[i].target = undefined;
                    }
                }
            }
        };
        nodeAddTarget(zNodes.children);

        $.fn.zTree.init($('#' + TREE_ID), setting, zNodes);
    };
    initTree();

    document.addEventListener('DOMContentLoaded', function() {
        document.querySelectorAll('.autocomplete');
    });
})();

$(document).ready(function(){
    let lastSearchNode = null;
    // 键盘释放：当输入框的键盘按键被松开时，把查询到的数据结果显示在标签中
    let $searchInput = $('#search-input');

    $searchInput.unbind('keyup');
    $searchInput.bind('keyup', function () {
        closeTree();

        // 搜索节点
        let zTree = $.fn.zTree.getZTreeObj(TREE_ID);
        let keyword = $('#search-input').val();
        if (!keyword) {
            updateNodes(lastSearchNode, false);
            return;
        }

        if (lastSearchNode) {
            updateNodes(lastSearchNode, false);
        }

        // 调用ztree的模糊查询功能，得到符合条件的节点
        let nodeList = zTree.getNodesByParamFuzzy('name', keyword);
        updateNodes(nodeList, true); //更新节点
        lastSearchNode = nodeList;
    });

    //高亮显示被搜索到的节点
    function updateNodes(nodeList, highlight) {
        let zTree = $.fn.zTree.getZTreeObj(TREE_ID);
        if (nodeList) {
            for (let i = 0, l = nodeList.length; i < l; i++) {
                // 将搜索到的节点的父节点展开
                zTree.expandNode(nodeList[i].getParentNode(), true, false, false);
                // 高亮显示搜索到的节点(highlight是自己设置的一个属性)
                nodeList[i].highlight = highlight;
                // 更新节点数据，主要用于该节点显示属性的更新
                zTree.updateNode(nodeList[i]);
            }
        }
    }

    function closeTree() {
        let tree = $.fn.zTree.getZTreeObj(TREE_ID);
        // 获取 zTree 的全部节点数据将节点数据转换为简单 Array 格式
        let nodes = tree.transformToArray(tree.getNodes());
        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].level === 0) {
                // 根节点展开
                tree.expandNode(nodes[i], true, true, false)
            } else {
                tree.expandNode(nodes[i], false, true, false)
            }
        }
    }
});
