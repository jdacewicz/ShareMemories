$(function () {
    $("#reactionForm").submit(function (e) {
        e.preventDefault();
        var frm = $("#reactionForm");
        var data = {};
        $.each(this, function (i, v) {
            var input = $(v);
            data[input.attr("name")] = input.val();
            delete data["undefined"];
        });
        saveRequestedData(frm, data);
    });

    $("#reactionReplaceForm").submit(function (e) {
        e.preventDefault();
        var frm = $("#reactionReplaceForm");
        var data = {};
        $.each(this, function (i, v) {
            var input = $(v);
            data[input.attr("name")] = input.val();
            delete data["undefined"];
        });
        saveRequestedData(frm, data);
    })
});

function saveRequestedData(frm, data) {
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: frm.attr("action"),
        type: frm.attr("method"),
        dataType: "json",
        data: JSON.stringify(data),
        success: function () {
            alert("Success");
        }
    });
}