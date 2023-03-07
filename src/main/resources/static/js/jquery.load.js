$(document).ready(function () {
    loadPosts();
    $("#posts").fadeIn("slow");

    $("#show-reaction-panel").click(function () {
        loadReactions();
        $("#main-content").hide();
        $("#reactions").show();
        $("#reactions-create").show();
        $("#panels").fadeIn("slow");
    });
});

function loadPosts() {
    $.ajax({
       type: "GET",
       url: "/api/posts/random",
       dataType: "JSON",
       success: function (data) {
           data.forEach(function (post) {
              showPost(post);
           });
       }
    });
}

function loadReactions() {
    $.ajax({
       type: "GET",
       url: "/api/reactions",
       dataType: "JSON",
       success: function (data) {
           data.forEach(function (reaction) {
               showReaction(reaction);
           });
       }
    });
}

function showPost(data) {
    var index = data.id;
    $("#posts").append(
        '<div name="post[' + index + ']" class="max-w-md mx-auto rounded-xl mb-4 bg-white p-4 grid grid-flow-row auto-rows-max shadow">' +
            '<div class="float-right"><span>' + data.elapsedCreationTimeMessage + '</span></div>' +
            '<div class="mt-2">' +
                '<span>' + data.content + '</span>' +
            '</div>' +
        '</div>'
    );
}

function showReaction(data) {
    var index = data.id;
    $("#reactions tbody").append(
        '<tr name="reaction[' + index + ']" class="bg-white border-b dark:bg-gray-800 dark:border-gray-700">' +
            '<th scope="row" class="px-6 py-4">' + data.image + '</th>' +
            '<td class="px-6 py-4">' + data.name + '</td>' +
            '<td class="px-6 py-4">' +
                '<button type="button" value="' + index + '" name="reaction-edit">EDIT</button>' +
                ' ' +
                '<button type="button" value="' + index + '" name="reaction-delete">DELETE</button>' +
            '</td>' +
        '</tr>'
    )
}
