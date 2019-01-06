$(document).ready(function(){
    $.ajax({
        url: "./../build/file/activities.json",
        type: "GET",
        dataType: "json",
        success: function(data) {
            var activities = data;
            createActivities(activities);
        }
     });
});
function createActivities(data){
    var activities_list = $("#activities_list");
    console.log(data.length);
    for(i=0;i<data.length;i++){
        var div_block = $('<div></div>');
        div_block.attr("class","block");
        var div_block_content = $('<div></div>');
        div_block_content.attr("class","block_content");
        var title = $('<h2></h2>');
        var alink = $('<a></a>');
        title.attr("class","title");
        alink.html(data[i].event);
        title.append(alink);
        var byline = $('<div></div>');
        byline.attr("class","byline");
        var span = $('<span></span>');
        var author = $('<a></a>');
        span.html("Time:"+ data[i].time);
        author.html('lyc');
        var excerpt = $('<p></p>');
        excerpt.attr('class','excerpt');
        excerpt.css("font-family","Times New Roman").css('font-style','normal').css('color','black');
        excerpt.html('Film festivals used to be do-or-die moments for movie makers. They were where you met the producers that could fund your project, and if the buyers liked your flick, they’d pay to Fast-forward and… '+'Read&nbsp;More');
        byline.append(span);
        byline.append(author);
        byline.append(excerpt);
        div_block_content.append(title);
        div_block_content.append(byline);
        div_block.append(div_block_content);
        activities_list.append(div_block);
    }
}
