$(function () {
    $("#show-reaction-panel").click(function () {
        $("#main-content").hide();
        $("#reaction-panel").show();
        $("#panels").fadeIn("slow");
    });

    $("#create-post-form").submit(function (e) {
        e.preventDefault();
        var frm = $("#create-post-form");
        var data = {};
        $.each(this, function (i, v) {
            var input = $(v);
            data[input.attr("name")] = input.val();
            delete data["undefined"];
        });
        saveRequestedData(frm, data);
    });

    $("#create-reaction-form").submit(function (e) {
        e.preventDefault();
        var frm = $("#create-reaction-form");
        var data = {};
        $.each(this, function (i, v) {
            var input = $(v);
            data[input.attr("name")] = input.val();
            delete data["undefined"];
        });
        saveRequestedData(frm, data);
    });
});

function saveRequestedData(frm, data) {
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: frm.attr("action"),
        type: frm.attr("method"),
        dataType: "json",
        data: JSON.stringify(data),
        success: function () {
            location.reload();
        }
    });
}