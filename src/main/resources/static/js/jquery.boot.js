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

    $("#posts").on("click", "button[name='comment-delete']", function () {
        let postDiv = $(this).closest("div[name^='post[']").attr("name");
        let postId = postDiv.substring(postDiv.indexOf("[") + 1, postDiv.indexOf("]"));
        let commentId = $(this).val();

        if (confirm('Comment ' + commentId + ' will be removed.')) {
            deletePostComment(postId, commentId);
        }
    });

    $("#show-reaction-panel").click(function () {
        if ($("#main-content").is(":visible")) {
            loadReactionPanelData();
            appendReactionCreateFormToPanel();

            $("#main-content").hide();
            $("#reactions").show();

            $("#panels").fadeIn("slow");
        }
    });

    $("#show-contact-form").click(function () {
        if ($("#main-content").is(":visible")) {
            appendContactForm();

            $("#main-content").hide();
            $("#reactions").hide();

            $("#panels").fadeIn("slow");
        }
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
    });

    $("#posts").on("mouseover", "div[name^='comment[']", function () {
        $(this).children().children().fadeIn("fast");
    });

    $("#posts").on("mouseleave", "div[name^='comment[']", function () {
        $(this).find("div[name^='reaction'] span:empty").parent().fadeOut("fast");
    });
});

function loadPosts() {
    let pathname = window.location.pathname;
    let url = "/api/posts/random";
    if (pathname.indexOf("profile") >= 0) {
        url = "/api/posts/user/" + (pathname.substring(pathname.lastIndexOf("/") + 1, pathname.length));
    }

    $.ajax({
       type: "GET",
       url: url,
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

                   $("div[name='post-reactions']").children().show();
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

function deletePostComment(postId, commentId) {
    $.ajax({
        type: "DELETE",
        url: "/api/comments/" + commentId + "/post/" + postId,
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
    let postImage = (post.imagePath == null) ? "":
        '<div class="mt-2">' +
            '<img src="' + post.imagePath + '">' +
        '</div>';

    $("#posts").append(
        '<div name="post[' + index + ']" class="max-w-md mx-auto rounded-xl mb-4 bg-white p-4 grid grid-flow-row auto-rows-max shadow">' +
            '<div class="block">' +
                '<div class="float-left">' +
                    '<a href="/profile/' + post.creator.id + '">' +
                        '<img class="rounded-full w-8 h-8 inline mr-2" src="' + post.creator.imagePath + '">' +
                        '<span class="font-bold">' + post.creator.firstname + ' ' + post.creator.lastname + '</span>' +
                    '</a>' +
                '</div>' +
                '<div class="float-right">' +
                    '<span>' + post.elapsedCreationTimeMessage + '</span>' +
                    '<button name="post-delete" value="' +  index + '">' +
                        '<img class="w-8" src="/images/icons/delete-icon.svg">' +
                    '</button>' +
                '</div>' +
            '</div>' +
            '<div class="mt-2">' +
                '<span>' + post.content + '</span>' +
            '</div>' +
            postImage +
            '<div name="post-reactions" class="grid grid-flow-col grid-cols-10 mt-2">' +
            '</div>' +
            '<div name="comments" class="border-t-2 mt-2">' +
            '</div>' +
            '<div class="mt-2">' +
                '<form action="/api/posts/' + index +'/comment" method="PUT" enctype="multipart/form-data" name="create-comment-form">' +
                        '<div class="w-full border border-gray-200 rounded-lg bg-gray-50 dark:bg-gray-700 dark:border-gray-600">' +
                            '<div class="px-4 py-2 bg-white rounded-t-lg dark:bg-gray-800">' +
                                '<textarea name="content" rows="2" class="w-full px-0 text-sm text-gray-900 bg-white border-0 dark:bg-gray-800 focus:ring-0 dark:text-white dark:placeholder-gray-400" placeholder="Write something..." required></textarea>' +
                                '<img src="">' +
                            '</div>' +
                            '<div class="flex items-center justify-between px-3 py-2 border-t dark:border-gray-600">' +
                                '<div class="flex pl-0 space-x-1 sm:pl-2">' +
                                    '<input name="image" type="file" class="inline-flex justify-center p-2 text-gray-500 rounded cursor-pointer hover:text-gray-900 hover:bg-gray-100 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600">' +
                                '</div>' +
                                '<button type="submit" class="inline-flex items-center py-2.5 px-4 text-xs font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-blue-200 dark:focus:ring-blue-900 hover:bg-blue-800">' +
                                    'Public' +
                                '</button>' +
                            '</div>' +
                        '</div>' +
                    '</button>' +
                '</form>' +
             '</div>' +
        '</div>'
    );
}

function appendReaction(reaction) {
    let index = reaction.id;
    $("div[name$='reactions']").append(
        '<div class="inline-block hidden" name="reaction[' + index + ']">' +
            '<span class="float-left"></span>' +
            '<button type="button" value="' +  index + '">' +
                '<img class="float-right w-12" src="' + reaction.imagePath + '">' +
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
            $("div[name='post[" + index + "]'] div[name='comment[" + comment.id + "]'] div[name='reaction[" + key + "]']").show();
        });
    });

}

function appendComment(postId, comment) {
    let index = comment.id;
    let image = (comment.imagePath == null) ? "" : '<img class="mx-auto h-1/2 w-1/2" src="' + comment.imagePath + '">';
    $("div[name='post[" + postId + "]'] div[name='comments']").append(
            '<div class="grid grid-flow-row auto-rows-max border rounded-md mt-2 pl-2 pr-2" name="comment[' + index + ']">' +
                '<div class="block mt-2">' +
                    '<div class="float-left">' +
                        '<img class="rounded-full w-6 h-6 inline mr-2" src="' + comment.creator.imagePath + '">' +
                        '<span class="font-bold">' + comment.creator.firstname + ' ' + comment.creator.lastname + '</span>' +
                    '</div>' +
                    '<div class="float-right">' +
                        '<span>' + comment.elapsedCreationTimeMessage + '</span>' +
                        '<button name="comment-delete" value="' +  index + '">' +
                            '<img class="w-8" src="/images/icons/delete-icon.svg">' +
                        '</button>' +
                    '</div>' +
                '</div>' +
                '<div class="block mt-2">' +
                    '<span>' + comment.content + '</span>' +
                '</div>' +
                '<div class="block mt-2">' +
                    image +
                '</div>' +
                '<div name="comment-reactions" class="grid grid-flow-col grid-cols-10">' +
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
                    '<img class="w-12" src="/images/icons/edit-icon.svg">' +
                '</button>' +
                '<button type="button" value="' + index + '" name="reaction-delete">' +
                    '<img class="w-12" src="/images/icons/delete-icon.svg">' +
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

function appendContactForm() {
    $("#panels").append(
        '<div id="contact" class="max-w-md mx-auto rounded-xl mb-4 bg-white p-4 grid grid-flow-row auto-rows-max shadow">' +
            '<div class="border p-4 rounded-lg">' +
                '<span class="block mb-2 text-sm uppercase font-medium text-gray-900 dark:text-white text-center">' +
                    'Contact Form' +
                '</span>' +
                '<form action="/contact" method="POST" enctype="multipart/form-data" id="contact-form">' +
                    '<div class="mb-6">' +
                        '<label for="contact-name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Name</label>' +
                        '<input type="text" id="contact-name" name="name" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="John Doe" required>' +
                    '</div>' +
                    '<div class="mb-6">' +
                        '<label for="contact-email" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Email</label>' +
                        '<input type="email" id="contact-email" name="email" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="john@exmaple.com" required>' +
                    '</div>' +
                    '<div class="mb-6">' +
                        '<label for="contact-phone" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Phone</label>' +
                        '<input type="text" id="contact-phone" name="phone" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="123-456-789" required>' +
                    '</div>' +
                    '<div class="mb-6">' +
                        '<label for="contact-topic" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Topic</label>' +
                        '<input type="text" id="contact-topic" name="topic" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="" required>' +
                    '</div>' +
                    '<div class="mb-6">' +
                        '<label for="contact-message" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Message</label>' +
                        '<textarea id="contact-message" name="message" rows="5" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Write something..." required></textarea>' +
                    '</div>' +
                    '<div class="mb-6">' +
                        '<label for="contact-file" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Files</label>' +
                        '<input type="file" id="contact-file" name="file" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="image.png">' +
                    '</div>' +
                    '<button type="submit" class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">' +
                        'Send' +
                    '</button>' +
                '</form>' +
            '</div>' +
        '</div>'
    );
}