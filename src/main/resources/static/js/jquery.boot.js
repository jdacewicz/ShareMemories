$(document).ready(function () {
    loadPosts();
    $("#posts").fadeIn("slow");

    $("input[name='image']").on("change", function () {
        let file = $("input[name='image']").get(0).files[0];

        if (file) {
            let reader = new FileReader();

            reader.onload = function () {
                $("#create-post-image").attr("src", reader.result);
            }
            reader.readAsDataURL(file);
        }
    });

    $("#show-reaction-panel").click(function () {
        loadReactionPanelData();
        $("#main-content").hide();
        $("#reactions").show();
        $("#reactions-create").show();
        $("#panels").fadeIn("slow");
    });

    $("#reactions").on("click", "button[name='reaction-delete']", function () {
        let id = $(this).val();
        deleteReaction(id);
    });
});

function loadPosts() {
    $.ajax({
       type: "GET",
       url: "/api/posts/random",
       dataType: "JSON",
       success: function (posts) {
           $.ajax({
               type: "GET",
               url: "/api/reactions",
               dataType: "JSON",
               success: function (reactions) {
                   posts.forEach(function (post) {
                       appendPost(post);
                   });
                   reactions.forEach(function (reaction) {
                       appendReaction(reaction);
                   });
                   posts.forEach(function (post) {
                       setPostReactionCount(post);
                   });
               }
           });
       }
    });
}

function loadReactionPanelData() {
    $.ajax({
       type: "GET",
       url: "/api/reactions",
       dataType: "JSON",
       success: function (data) {
           data.forEach(function (reaction) {
               appendReactionDataToPanel(reaction);
           });
       }
    });
}

function deleteReaction(id) {
    $.ajax({
       type: "DELETE",
       url: "/api/reactions/" + id,
       success: function () {
           alert("Success");
       }
    });
}

function appendPost(post) {
    let index = post.id;
    $("#posts").append(
        '<div name="post[' + index + ']" class="max-w-md mx-auto rounded-xl mb-4 bg-white p-4 grid grid-flow-row auto-rows-max shadow">' +
            '<div class="float-right"><span>' + post.elapsedCreationTimeMessage + '</span></div>' +
            '<div class="mt-2">' +
                '<span>' + post.content + '</span>' +
            '</div>' +
            '<div name="reactions" class="grid grid-flow-col grid-cols-6 mt-2">' +
            '</div>' +
        '</div>'
    );
}

function appendReaction(reaction) {
    let index = reaction.id;
    $("div[name='reactions']").append(
        '<div name="reaction[' + index + ']">' +
            '<button type="button" value="' +  index + '">' +
                '<span></span>' +
                '<img src="' + reaction.image + '">' +
            '</button>' +
        '</div>'
    );
}

function setPostReactionCount(post) {
    let index = post.id;
    $.each(post.reactionsCounts ,function (key, value) {
        $("div[name='post[" + index + "]'] div[name='reactions'] div[name='reaction[" + key + "]'] span").text(value);
    });
}

function appendReactionDataToPanel(reaction) {
    let index = reaction.id;
    $("#reactions tbody").append(
        '<tr name="reaction[' + index + ']" class="bg-white border-b dark:bg-gray-800 dark:border-gray-700">' +
            '<th scope="row" class="px-6 py-4">' + reaction.image + '</th>' +
            '<td class="px-6 py-4">' + reaction.name + '</td>' +
            '<td class="px-6 py-4">' +
                '<button type="button" value="' + index + '" name="reaction-edit">EDIT</button>' +
                ' ' +
                '<button type="button" value="' + index + '" name="reaction-delete">DELETE</button>' +
            '</td>' +
        '</tr>'
    )
}
