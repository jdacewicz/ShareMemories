$(document).ready(function () {
    loadPosts();
    $("#posts").fadeIn("slow");
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
