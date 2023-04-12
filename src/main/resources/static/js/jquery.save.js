$(function () {
    $("#create-post-form").submit(function (e) {
        e.preventDefault();
        let frm = $('#create-post-form');
        let data = new FormData($('#create-post-form')[0]);

        saveMultipartRequestedData(frm, data)
    });

    $("#posts").on("submit", "form[name='create-comment-form']", function (e) {
        e.preventDefault();
        let frm = $("form[name='create-comment-form']");
        let data = new FormData($("form[name='create-comment-form']")[0]);

        saveMultipartRequestedData(frm, data);
    })

    $("#panels").on("submit", "#create-reaction-form", function (e) {
        e.preventDefault();
        let frm = $("#create-reaction-form");
        let data = new FormData($('#create-reaction-form')[0]);

        saveMultipartRequestedData(frm, data)
    });

    $("#panels").on("submit", "#update-reaction-form", function (e) {
        e.preventDefault();
        let frm = $("#update-reaction-form");
        let data = new FormData($('#update-reaction-form')[0]);

        saveMultipartRequestedData(frm, data)
    });

    $("#chat-message-form").submit(function (e) {
        e.preventDefault();

        let frm = $("#chat-message-form form");
        let data = new FormData($('#chat-message-form form')[0]);

        saveMultipartRequestedData(frm, data)
    });

    $("#posts").on("click", "div[name='post-reactions'] button", function (e) {
        e.preventDefault();
        let postDiv = $(this).closest("div[name^='post[']").attr("name");
        let postId = postDiv.substring(postDiv.indexOf("[") + 1, postDiv.indexOf("]"));
        let reactionId = $(this).val();

        let url = "/api/posts/" + postId + "/react/" + reactionId;
        saveRequestedData(url, "PUT");
    });

    $("#posts").on("click", "div[name='comment-reactions'] button", function (e) {
        e.preventDefault();
        let commentDiv = $(this).closest("div[name^='comment[']").attr("name");
        let commentId = commentDiv.substring(commentDiv.indexOf("[") + 1, commentDiv.indexOf("]"));
        let reactionId = $(this).val();

        let url = "/api/comments/" + commentId + "/react/" + reactionId;
        saveRequestedData(url, "PUT");
    });

    $("button[name='add-to-friends']").click(function () {
        let id = $(this).val();

        let url = "/api/users/invite/" + id;
        saveRequestedData(url, "PUT");
    });
});

function saveRequestedData(action, method) {
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: action,
        type: method,
        dataType: "json",
        success: function () {
            location.reload();
        }
    });
}

function saveMultipartRequestedData(frm, data) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: frm.attr("action"),
        type: frm.attr("method"),
        data : data,
        processData : false,
        contentType : false,
        success : function() {
            location.reload();
        }
    });
}