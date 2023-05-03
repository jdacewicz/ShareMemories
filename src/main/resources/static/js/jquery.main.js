$(document).ready(function () {
    loadPosts();

    $("#main-content").on("click", ".upload-image-button", function () {
        let fileDialog = $(this).parent().find("input[type='file']");
        let img = $(this).parent().parent().parent().find(".image-preview").first();

        showFileDialogAndPreviewImage(fileDialog, img);
    });

    $("#create-post-form").submit(function (e) {
       e.preventDefault();

        let frm = $(this);
        let data = new FormData($(this)[0])

       savePost(frm, data);
    });

    $("#posts").on("submit", ".create-comment-form", function (e) {
        e.preventDefault();

        let frm = $(this);
        let method = $(this).attr("action");
        let data = new FormData($(this)[0])

        saveComment(frm, data, method.substring(method.lastIndexOf('/') + 1));
    });

    $("#posts").on("mouseenter", ".comment-reactions", function () {
        let reactions = $(this).children(".reactions");
        $(this).children(".react-button").fadeOut("fast", function () {
            reactions.fadeIn("fast");
        });
    })

    $("#posts").on("mouseleave", ".comment-reactions", function () {
        let button = $(this).children(".react-button");
        $(this).children(".reactions").fadeOut("fast", function () {
            button.fadeIn("fast");
        });
    })
})

function savePost(frm, data) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: frm.attr("action"),
        type: "POST",
        data : data,
        dataType: "JSON",
        processData : false,
        contentType : false,
        success : function(post) {
            appendPost(post);
            $("div[id='post[" + post.id + "]']").insertBefore("#posts div:eq(0)");
        }
    });
}

function saveComment(frm, data, postId) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: frm.attr("action"),
        type: "PUT",
        data : data,
        dataType: "JSON",
        processData : false,
        contentType : false,
        success : function(comment) {
            appendComment(postId, comment);
        }
    });
}

function loadPosts() {
    $.ajax({
        type: "GET",
        url: "/api/posts/random",
        dataType: "JSON",
        success: function (posts) {
            if (posts == null) {
                return;
            }

            $.ajax({
                type: "GET",
                url: "/api/reactions",
                dataType: "JSON",
                success: function (reactions) {
                    posts.forEach(function (post) {
                        appendPost(post);
                        post.comments.forEach(function (comment) {
                           appendComment(post.id, comment);
                        });
                    });

                    if (reactions != null) {
                        reactions.forEach(function (reaction) {
                            appendReaction(reaction);
                        });
                    }

                    $("#posts").fadeIn("fast");
                }
            });
        }
    });
}

function showFileDialogAndPreviewImage(fileInput, imgTag) {
    fileInput.click();
    fileInput.on("change", function () {
        let file = fileInput.get(0).files[0];
        let reader = new FileReader();

        reader.onload = function () {
            imgTag.attr("src", reader.result).show();
        }
        reader.readAsDataURL(file);
    })
}

function appendPost(post) {
    let postImage = (post.imagePath == null) ? "" :
        '<img src="' + post.imagePath + '" alt="post picture">';

    $("#posts").append(
        '<div id="post[' + post.id + ']" class="w-full bg-white rounded-xl shadow mb-4">' +
            '<div class="w-full">' +
                '<div class="w-full border-b">' +
                    '<div class="flex justify-between p-2">' +
                        '<div class="flex justify-start">' +
                            '<div class="mt-1 mr-1">' +
                                '<img src="' + post.creator.imagePath + '" class="w-8 rounded-xl mx-2 border" alt="user profile picture">' +
                            '</div>' +
                            '<div class="text-sm">' +
                                '<a href="/profile/' + post.creator.id + '" class="block font-medium hover:underline">' +
                                    '<span>' + post.creator.capitalizedFirstAndLastName + '</span>' +
                                '</a>' +
                                '<span class="block text-gray-400">2 hours ago.</span>' +
                            '</div>' +
                        '</div>' +
                        '<div>' +
                            '<button type="button">' +
                                '<img src="/images/icons/show-more-icon.svg" class="w-8" alt="show more icon">' +
                            '</button>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<div class="w-full py-2 px-4">' +
                    '<span class="text-sm">' +
                        post.content +
                    '</span>' +
                '</div>' +
                '<div class="w-full">' +
                    postImage +
                '</div>' +
                '<div class="reactions flex items-center justify-start p-2 border-b">' +
                '</div>' +
            '</div>' +
            '<div>' +
                '<div class="comments">' +
                '</div>' +
                '<div>' +
                    '<form class="create-comment-form" action="/api/comments/post/' + post.id + '" enctype="multipart/form-data">' +
                        '<div class="flex justify-between p-2">' +
                            '<div class="mt-1 mr-2">' +
                                '<img src="' + post.creator.imagePath + '" class="w-8 rounded-xl mx-2 border" alt="user profile picture">' +
                            '</div>' +
                            '<div class="flex items-center justify-start text-sm w-full border rounded-xl">' +
                                '<input name="image" type="file" hidden>' +
                                '<div class="w-full bg-gray-100 border-r rounded-l-xl p-2">' +
                                    '<textarea name="content" class="w-full bg-gray-100 resize-y" rows="1" placeholder="Write something..."></textarea>' +
                                    '<img src="#" alt="comment uploaded image preview" class="image-preview hidden">' +
                                '</div>' +
                                '<button type="button" class="upload-image-button mx-1">' +
                                    '<img src="/images/icons/image-icon.svg" class="w-8" alt="image icon">' +
                                '</button>' +
                            '</div>' +
                            '<div class="flex items-center ml-1">' +
                                '<button type="submit" class="px-2">' +
                                    '<img src="/images/icons/arrow-right-icon.svg" class="w-10" alt="send icon">' +
                                '</button>' +
                            '</div>' +
                        '</div>' +
                    '</form>' +
                '</div>' +
            '</div>' +
        '</div>'
    );
}

function appendComment(postId, comment) {
    $("div[id='post[" + postId + "]'] .comments").append(
        '<div class="flex justify-between p-2">' +
            '<div class="flex justify-start">' +
                '<div class="mt-1 mr-1">' +
                    '<img src="' + comment.creator.imagePath + '" class="w-8 rounded-xl mx-2 border" alt="user profile picture">' +
                '</div>' +
                '<div class="text-sm p-2 mr-1 rounded-xl border bg-gray-100">' +
                    '<a href="/profile/' + comment.creator.id + '" class="block font-medium hover:underline">' +
                        '<span>' + comment.creator.capitalizedFirstAndLastName + '</span>' +
                    '</a>' +
                    '<span class="block">' + comment.content + '</span>' +
                    '<span class="block text-gray-400">' + comment.elapsedCreationTimeMessage + '</span>' +
                '</div>' +
            '</div>' +
            '<div>' +
                '<div class="comment-reactions flex justify-start items-center text-sm font-medium">' +
                    '<div class="react-button">' +
                        '<span>React</span>' +
                        '<img src="/images/icons/arrow-down-icon.svg" class="w-8" alt="show more icon">' +
                    '</div>' +
                    '<div class="reactions hidden px-2"></div>' +
                '</div>' +
            '</div>' +
        '</div>'
    );
}

function appendReaction(reaction) {
    $(".reactions").append(
        '<div class="reaction[' + reaction.id + '] w-8 m-2">' +
            '<img src="' + reaction.imagePath + '" alt="' + reaction.name + ' reaction">' +
        '</div>'
    );
}