var pageUrl = "/saleCount/listGroupCount.html";

$(function(){
	queryFY();
});

/**
 * 填充表格数据
 * @param pageInfo  ajax返回的参数信息
 */
function showTable(pageInfo) {
    var beans = pageInfo.data
    $("#tbody").html("");//清空表格中数据并重新填充数据
    for(var i=0,length_1 = beans.length;i<length_1;i++){
        var tr = "<tr>"
            +'<td>'+beans[i].index+'</td>'
            +'<td>'+replaceNull(beans[i].area)+'</td>'
            +'<td>'+replaceNull(beans[i].yearNum) + "-" + replaceNull(beans[i].monthNum) +'</td>'
            +'<td>'+replaceNull(beans[i].saleUsername)+'</td>'
            +'<td>'+replaceNull(beans[i].saleFullname)+'</td>'
            +'<td>'+replaceNull(beans[i].planSaleAmount)+'</td>'
            +'<td>'+replaceNull(beans[i].actuallySaleAmount)+'</td>'
            +'<td>'+replaceNull(beans[i].planAwayAmount)+'</td>'
            +'<td>'+replaceNull(beans[i].actuallyAwayAmount)+'</td>'
            +'<td>'+replaceNull(beans[i].planSocialAmount)+'</td>'
            +'<td>'+replaceNull(beans[i].actuallySocialAmount)+'</td>'
            +'<td>'+replaceNull(beans[i].actuallyGroupSaleAmount) + "/" + replaceNull(beans[i].planGroupSaleAmount) +'</td>'
            +'<td>'+replaceNull(beans[i].groupSaleRatio)+'</td>'
            +'<td>'+replaceNull(beans[i].groupPerformance)+'</td>'
            +'<td>'+replaceNull(beans[i].selfPerformance)+'</td>'
            +'<td>'+replaceNull(beans[i].thisMonthRemainAwayAmount) + "/" + replaceNull(beans[i].lastMonthRemainAwayAmount) +'</td>'
            +'<td>'+replaceNull(beans[i].thisMonthRemainSocialAmount) + "/" + replaceNull(beans[i].lastMonthRemainSocialAmount) +'</td>'
            +'<td>'+replaceNull(beans[i].groupBuildCosts) + '</td>'
        	+ '</tr>';
        		
        $("#tbody").append(tr);
    }
}