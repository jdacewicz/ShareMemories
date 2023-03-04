$(function () {
    $("#reactionForm").submit(function (e) {
        e.preventDefault();
        var data = {};
        $.each(this, function (i, v) {
            var input = $(v);
            data[input.attr("name")] = input.val();
            delete data["undefined"];
        });
        saveRequestedData(data);
    });
});

function saveRequestedData(data) {
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "http://localhost:8080/api/reactions",
        type: "POST",
        dataType: "json",
        data: JSON.stringify(data),
        success: function () {
            alert("Success");
        }
    });
}