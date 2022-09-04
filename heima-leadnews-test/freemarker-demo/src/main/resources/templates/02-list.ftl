<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>

<#-- list 数据的展示 -->
<b>展示list中的stu数据:</b>
<br>
<br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>

    <#if stus??>
        <#list stus as stu>
            <#if stu.name='小红'>
                <tr style="color: deeppink">
                    <td>${stu_index + 1}</td>
                    <td>${stu.name}</td>
                    <td>${stu.age}</td>
                    <td>${stu.money}</td>
                </tr>
            <#else >
                <tr style="color: cyan">
                    <td>${stu_index + 1}</td>
                    <td>${stu.name}</td>
                    <td>${stu.age}</td>
                    <td>${stu.money}</td>
                </tr>
            </#if>
        </#list>
    </#if>

    stus集合的大小: ${stus?size}

</table>
<hr>

<#-- Map 数据的展示 -->
<b>map数据的展示：</b>
<br/><br/>
<a href="###">方式一：通过map['keyname'].property</a><br/>
输出stu1的学生信息：<br/>
姓名：${stuMap['stu1'].name}<br/>
年龄：${stuMap['stu1'].age}<br/>
<br/>
<a href="###">方式二：通过map.keyname.property</a><br/>
输出stu2的学生信息：<br/>
姓名：${stuMap.stu2.name}<br/>
年龄：${stuMap.stu2.age}<br/>

<br/>
<a href="###">遍历map中两个学生信息：</a><br/>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stuMap?keys as key>
        <tr>
            <td>${key_index}</td>
            <td>${stuMap[key].name}</td>
            <td>${stuMap[key].age}</td>
            <td>${stuMap[key].money}</td>
        </tr>
    </#list>
</table>
<hr>
<b>算数运算符</b>
<br/><br/>
100+5 运算：  ${100 + 5 }<br/>
100 - 5 * 5运算：${100 - 5 * 5}<br/>
5 / 2运算：${5 / 2}<br/>
12 % 10运算：${12 % 10}<br/>
<hr>

显示年月日：${now?date}<br>
显示时分秒：${now?time}<br>
显示年月日时分秒：${now?datetime}<br>
自定义格式化：${now?string("yyyy年MM月")}<br>
内建函数`c` 数字：${point}<br>
内建函数`c` 字符串：${point?c}

<hr>

<b>声明变量assign</b><br>
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank}  账号：${data.account}

<hr>

</body>
</html>