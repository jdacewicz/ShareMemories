$(document).ready(function () {
    loadPosts();
    $("#posts").fadeIn("slow");

    $("#create-post-image").on("change", function () {
        let file = $("#create-post-image").get(0).files[0];

        if (file) {
            let reader = new FileReader();

            reader.onload = function () {
                $("#create-post-image-preview").attr("src", reader.result);
            }
            reader.readAsDataURL(file);
        }
    });

    $("#panels").on("change", "#create-reaction-image",function () {
        let file = $("#create-reaction-image").get(0).files[0];

        if (file) {
            let reader = new FileReader();

            reader.onload = function () {
                $("#create-reaction-image-preview").attr("src", reader.result);
            }
            reader.readAsDataURL(file);
        }
    });

    $("#panels").on("change", "#update-reaction-image",function () {
        let file = $("#update-reaction-image").get(0).files[0];

        if (file) {
            let reader = new FileReader();

            reader.onload = function () {
                $("#update-reaction-image-preview").attr("src", reader.result);
            }
            reader.readAsDataURL(file);
        }
    });

    $("#posts").on("click", "button[name='post-delete']", function () {
        let id = $(this).val();
        if (confirm('Post ' + id + ' will be removed.')) {
            deletePost(id);
        }
    });

    $("#show-reaction-panel").click(function () {
        loadReactionPanelData();
        appendReactionCreateFormToPanel();

        $("#main-content").hide();
        $("#reactions").show();

        $("#panels").fadeIn("slow");
    });

    $("#reactions").on("click", "button[name='reaction-delete']", function () {
        let id = $(this).val();
        if (confirm('Are You sure You want to delete this reaction?')) {
            deleteReaction(id);
        }
    });

    $("#reactions").on("click", "button[name='reaction-edit']", function () {
        $("#reactions-create").remove();

        let id = $(this).val();
        loadReactionDetails(id);
    })
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
                       post.comments.forEach(function (comment) {
                           appendComment(post.id, comment);
                       })
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

function loadReactionDetails(id) {
    $.ajax({
        type: "GET",
        url: "/api/reactions/" + id,
        dataType: "JSON",
        success: function (reaction) {
            appendReactionEditFormToPanel(reaction);
        }
    });
}

function deletePost(id) {
    $.ajax({
        type: "DELETE",
        url: "/api/posts/" + id,
        success: function () {
            location.reload();
        }
    });
}

function deleteReaction(id) {
    $.ajax({
       type: "DELETE",
       url: "/api/reactions/" + id,
       success: function () {
           location.reload();
       }
    });
}

function appendPost(post) {
    let index = post.id;
    let image = (post.imagePath == null) ? "":
        '<div class="mt-2">' +
            '<img src="' + post.imagePath + '">' +
        '</div>';

    $("#posts").append(
        '<div name="post[' + index + ']" class="max-w-md mx-auto rounded-xl mb-4 bg-white p-4 grid grid-flow-row auto-rows-max shadow">' +
            '<div class="float-right">' +
                '<span>' + post.elapsedCreationTimeMessage + '</span>' +
                '<button name="post-delete" value="' +  index + '">' +
                    '<img class="w-8" src="/icons/delete-icon.svg">' +
                '</button>' +
            '</div>' +
            '<div class="mt-2">' +
                '<span>' + post.content + '</span>' +
            '</div>' +
            image +
            '<div name="post-reactions" class="grid grid-flow-col grid-cols-10 mt-2">' +
            '</div>' +
            '<div name="comments" class="border-t-2 mt-2">' +
            '</div>' +
        '</div>'
    );
}

function appendReaction(reaction) {
    let index = reaction.id;
    $("div[name$='reactions']").append(
        '<div name="reaction[' + index + ']">' +
            '<button type="button" value="' +  index + '">' +
                '<img src="' + reaction.imagePath + '">' +
                '<span></span>' +
            '</button>' +
        '</div>'
    );
}

function setPostReactionCount(post) {
    let index = post.id;
    $.each(post.reactionsCounts, function (key, value) {
        $("div[name='post[" + index + "]'] div[name='post-reactions'] div[name='reaction[" + key + "]'] span").text(value);
    });
    post.comments.forEach(function (comment) {
        $.each(comment.reactionsCounts, function (key, value) {
            $("div[name='post[" + index + "]'] div[name='comment[" + comment.id + "]'] div[name='reaction[" + key + "]'] span").text(value);
        });
    });

}

function appendComment(postId, comment) {
    let index = comment.id;
    let image = (comment.imagePath == null) ? "" : '<img class="h-1/5 w-1/5" src="' + comment.imagePath + '">';
    $("div[name='post[" + postId + "]'] div[name='comments']").append(
        '<div class="mt-2" name="comment[' + index + ']">' +
            '<span>' + comment.content + '</span>' +
            image +
            '<div name="comment-reactions" class="grid grid-flow-col grid-cols-10 mt-2">' +
            '</div>' +
        '</div>'
    );
}

function appendReactionDataToPanel(reaction) {
    let index = reaction.id;
    $("#reactions tbody").append(
        '<tr name="reaction[' + index + ']" class="bg-white border-b dark:bg-gray-800 dark:border-gray-700">' +
            '<th scope="row" class="px-4 py-4"><img class="w-14" src="' + reaction.imagePath + '"></th>' +
            '<td class="px-4 py-4">' + reaction.name + '</td>' +
            '<td class="px-4 py-4">' +
                '<button type="button" value="' + index + '" name="reaction-edit">' +
                    '<img class="w-12" src="/icons/edit-icon.svg">' +
                '</button>' +
                '<button type="button" value="' + index + '" name="reaction-delete">' +
                    '<img class="w-12" src="/icons/delete-icon.svg">' +
                '</button>' +
            '</td>' +
        '</tr>'
    )
}

function appendReactionCreateFormToPanel() {
    $("#panels").append(
        '<div id="reactions-create" class="max-w-md mx-auto rounded-xl mb-4 bg-white p-4 grid grid-flow-row auto-rows-max shadow">' +
            '<div class="border p-4 rounded-lg">' +
                '<span class="block mb-2 text-sm uppercase font-medium text-gray-900 dark:text-white text-center">' +
                    'Create reaction' +
                '</span>' +
            '<form action="/api/reactions" method="POST" enctype="multipart/form-data" id="create-reaction-form">' +
                '<div class="mb-6">' +
                    '<label for="reaction-name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Name</label>' +
                    '<input type="text" id="reaction-name" name="name" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="e.g. Love" required>' +
                '</div>' +
                '<div class="mb-6">' +
                    '<img id="create-reaction-image-preview" src="">' +
                    '<label for="create-reaction-image" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Image</label>' +
                    '<input type="file" id="create-reaction-image" name="image" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="image.png" required>' +
                '</div>' +
                '<button type="submit" class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">' +
                    'Create' +
                '</button>' +
            '</form>' +
            '</div>' +
        '</div>'
    );
}

function appendReactionEditFormToPanel(reaction) {
    let editForm = $("#panels #reaction-edit");
    editForm.remove();

    $("#panels").append(
        '<div id="reaction-edit" class="max-w-md mx-auto rounded-xl mb-4 bg-white p-4 grid grid-flow-row auto-rows-max shadow">' +
            '<div class="border p-4 rounded-lg">' +
                '<span class="block mb-2 text-sm uppercase font-medium text-gray-900 dark:text-white text-center">' +
                    'Edit reaction: ' + reaction.name +
                '</span>' +
                '<form action="/api/reactions/' + reaction.id + '" method="PUT" encType="multipart/form-data" id="update-reaction-form">' +
                    '<div class="mb-6">' +
                        '<label for="update-reaction-name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Name</label>' +
                        '<input type="text" value="' + reaction.name + '" id="update-reaction-name" name="name" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" required>' +
                    '</div>' +
                    '<div class="mb-6">' +
                        '<img id="update-reaction-image-preview" src="">' +
                        '<label for="update-reaction-image" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Image</label>' +
                        '<input type="file" id="update-reaction-image" name="image" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">' +
                    '</div>' +
                    '<button type="submit" class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">' +
                        'Edit' +
                    '</button>' +
                '</form>' +
            '</div>' +
        '</div>'
    );
}

