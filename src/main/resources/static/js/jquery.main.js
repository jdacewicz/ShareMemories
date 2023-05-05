$(document).ready(function () {
    let posts = $("#posts");
    let mainContent = $("#main-content");
    let adminPanel = $("#admin-panel");
    let panels = $("#panels");

    loadPosts();

    mainContent.on("click", ".upload-image-button", function () {
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

    posts.on("submit", ".create-comment-form", function (e) {
        e.preventDefault();

        let frm = $(this);
        let method = $(this).attr("action");
        let data = new FormData($(this)[0])

        saveComment(frm, data, method.substring(method.lastIndexOf('/') + 1));
    });

    posts.on("mouseenter", ".comment-reactions", function () {
        let reactions = $(this).children(".reactions");

        $(this).children(".react-button").fadeOut("fast", function () {
            reactions.fadeIn("fast");
        });
    })

    posts.on("mouseleave", ".comment-reactions", function () {
        let button = $(this).children(".react-button");

        $(this).children(".reactions").fadeOut("fast", function () {
            button.fadeIn("fast");
        });
    })

    posts.on("click", ".post-main button[class^='reaction'], .comment-reactions button[class^='reaction']", function () {
        let reactionId = getIdFromSquareBracket($(this).attr("class"));

        if ($(this).closest(".comment-reactions").length > 0) {
            let commentId = getIdFromSquareBracket($(this).closest("div[id^='comment[']").attr("id"));
            updatePost("/api/comments/" + commentId + "/react/" + reactionId);
        } else {
            let postId = getIdFromSquareBracket($(this).closest("div[id^='post[']").attr("id"));
            updatePost("/api/posts/" + postId + "/react/" + reactionId);
        }

        let counter = $(this).children("span");
        if (counter.text().length === 0) {
            counter.text(0);
        }

        counter.text(parseInt(counter.text()) + 1);
        counter.fadeIn("fast");
    });

    posts.on("click", ".show-more-button", function () {
        $(this).fadeOut("fast", function () {
            $(this).parent().children(".more-options").fadeIn("fast");
        });
    });

    posts.on("click", ".close-window-button", function () {
        let parent = $(this).parent();
        parent.fadeOut("fast", function () {
            parent.parent().children(".show-more-button").fadeIn("fast");
        });
    });

    posts.on("click", ".delete-post-button", function () {
        let postId = getIdFromSquareBracket($(this).closest("div[id^='post[']").attr("id"));
        let post = $(this).closest("div[id^='post[']");

        deleteObject("/api/posts/" + postId);
        post.fadeOut("fast", function () {
            post.remove();
        });
    });

    $("#show-contact-form").click(function () {
        if (mainContent.is(":visible")) {
            mainContent.fadeOut("fast", function () {
                adminPanel.hide();
                $("#contact-panel").show();
                panels.fadeIn("fast");
            });
        } else {
            panels.fadeOut("fast", function () {
                adminPanel.hide();
                $("#contact-panel").show();
                panels.fadeIn("fast");
            });
        }
    });

    $("#show-admin-users-panel").click(function () {
        if (mainContent.is(":visible")) {
            mainContent.fadeOut("fast", function () {
                $("#contact-panel").hide();
                adminPanel.show();
                $("#admin-users-panel").show();
                panels.fadeIn("fast");
            });
        } else {
            panels.fadeOut("fast", function () {
                $("#contact-panel").hide();
                adminPanel.show();
                adminPanel.children().hide();
                $("#admin-users-panel").fadeIn("fast");
                panels.fadeIn("fast");
            });
        }
    });

    $("#show-admin-reactions-panel").click(function () {
        if (mainContent.is(":visible")) {
            $("#admin-reactions-list tbody").empty();
            loadAdminPanelReactions();

            mainContent.fadeOut("fast", function () {
                $("#contact-panel").hide();
                adminPanel.show();
                $("#admin-reactions-panel").show();
                panels.fadeIn("fast");
            });
        } else {
            $("#admin-reactions-list tbody").empty();
            loadAdminPanelReactions();

            panels.fadeOut("fast", function () {
                $("#contact-panel").hide();
                adminPanel.show();
                adminPanel.children().hide();
                $("#admin-reactions-panel").fadeIn("fast");
                panels.fadeIn("fast");
            });
        }
    });

    $("#admin-user-search").submit(function (e) {
        e.preventDefault();
        let userId = $("#admin-user-search input").val();

        $("#admin-user-search-results").fadeIn("fast");
        loadUser(userId);
    });

    $("#admin-user-search-results").on("click", ".delete-user", function () {
        if (confirm('Are you sure you want to delete this user?')) {
            let userId = getIdFromSquareBracket($(this).closest("tr[class^='user[']").attr("class"));

            deleteObject("/api/users/" + userId);
            $("#admin-user-search-results tr[class^='user[" + userId + "]']").remove();
        }
    });

    $("#admin-search-results-clear").click(function () {
        $("#admin-user-search-results table tbody").empty();
    });

    $("#admin-reactions-list").on("click", ".delete-reaction", function () {
        if (confirm('Are you sure you want to delete this reaction?')) {
            let reactionId = getIdFromSquareBracket($(this).closest("tr[class^='reaction[']").attr("class"));

            deleteObject("/api/reactions/" + reactionId);
            $("#admin-reactions-list tr[class^='reaction[" + reactionId + "]']").remove();
        }
    });
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

function updatePost(url) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: url,
        type: "PUT",
        dataType: "JSON",
        processData : false,
        contentType : false
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

                        posts.forEach(function (post) {
                            updatePostReactionCount(post);
                            post.comments.forEach(function (comment) {
                                updateCommentReactionCount(post.id, comment);
                            });
                        });
                    }

                    $("#posts").fadeIn("fast");
                }
            });
        }
    });
}

function loadUser(id) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: "/api/users/" + id,
        type: "GET",
        dataType: "JSON",
        processData : false,
        contentType : false,
        success: function (user) {
            appendUserToAdminPanel(user);
        }
    });
}

function loadAdminPanelReactions() {
    $.ajax({
        enctype : 'multipart/form-data',
        url: "/api/reactions",
        type: "GET",
        dataType: "JSON",
        processData : false,
        contentType : false,
        success: function (reactions) {
            reactions.forEach(function (reaction) {
                appendReactionToAdminPanel(reaction);
            });
        }
    });
}

function deleteObject(url) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: url,
        type: "DELETE",
        dataType: "JSON",
        processData : false,
        contentType : false
    });
}

function getIdFromSquareBracket(str) {
    return str.substring(str.indexOf('[') + 1, str.indexOf(']'));
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

function updatePostReactionCount(post) {
    $.each(post.reactionsCounts, function (key, value) {
        $("div[id='post[" + post.id + "]'] .post-main button[class^='reaction[" + key + "]'] span").text(value).fadeIn("fast");
    });
}

function updateCommentReactionCount(postId, comment) {
    $.each(comment.reactionsCounts, function (key, value) {
        $("div[id='post[" + postId + "]'] div[id='comment[" + comment.id + "]'] button[class^='reaction[" + key + "]'] span").text(value).fadeIn("fast");
    });
}

function appendPost(post) {
    let postImage = (post.imagePath == null) ? "" :
        '<img src="' + post.imagePath + '" alt="post picture">';

    $("#posts").append(
        '<div id="post[' + post.id + ']" class="w-full bg-white rounded-xl shadow mb-4">' +
            '<div class="post-main w-full">' +
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
                                '<span class="block text-gray-400">' + post.elapsedCreationTimeMessage + '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div>' +
                            '<button class="show-more-button" type="button">' +
                                '<img src="/images/icons/show-more-icon.svg" class="w-8" alt="show more icon">' +
                            '</button>' +
                            '<div class="more-options fixed hidden bg-white rounded-xl shadow">' +
                                '<button class="close-window-button block py-1 px-2">' +
                                    '<img class="w-6" src="/images/icons/close-icon.svg" alt="close icon">' +
                                '</button>' +
                                '<button class="delete-post-button block text-sm hover:bg-gray-100 py-1 px-4" type="button">' +
                                    '<span>Delete post</span>' +
                                '</button>' +
                            '</div>' +
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
    let commentImage = (comment.imagePath == null) ? "" :
        '<img src="' + comment.imagePath + '" class="block py-2" alt="post picture">';

    $("div[id='post[" + postId + "]'] .comments").append(
        '<div id="comment[' + comment.id + ']" class="flex justify-between p-2">' +
            '<div class="flex justify-start">' +
                '<div class="mt-1 mr-1">' +
                    '<img src="' + comment.creator.imagePath + '" class="w-8 rounded-xl mx-2 border" alt="user profile picture">' +
                '</div>' +
                '<div class="text-sm p-2 mx-1 w-full rounded-xl border bg-gray-100">' +
                    '<a href="/profile/' + comment.creator.id + '" class="block font-medium hover:underline">' +
                        '<span>' + comment.creator.capitalizedFirstAndLastName + '</span>' +
                    '</a>' +
                    '<span class="block">' + comment.content + '</span>' +
                    commentImage +
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
        '<button class="reaction[' + reaction.id + '] flex items-center w-8 m-2">' +
            '<span class="hidden"></span>' +
            '<img src="' + reaction.imagePath + '" alt="' + reaction.name + ' reaction">' +
        '</button>'
    );
}

function appendUserToAdminPanel(user) {
    if ($("#admin-user-search-results tr[class^='user[" + user.id + "]']").length > 0) {
        return;
    }

    $("#admin-user-search-results tbody").append(
        '<tr class="user[' + user.id +'] border-b">' +
            '<th scope="row" class="px-4 py-2">' +
                '<a href="/profile/' + user.id + '" class="flex justify-center items-center font-medium hover:underline">' +
                    '<img src="' + user.imagePath + '" class="w-6 rounded-lg mr-2" alt="user profile picture">' +
                    '<span>' + user.capitalizedFirstAndLastName + '</span>' +
                '</a>' +
            '</th>' +
            '<td class="px-4 py-2">' +
                '<div class="flex justify-center items-center">' +
                    '<button class="delete-user" type="button">' +
                        '<img src="/images/icons/close-icon.svg" class="w-8" alt="delete icon">' +
                    '</button>' +
                '</div>' +
            '</td>' +
        '</tr>'
    );
}

function appendReactionToAdminPanel(reaction) {
    $("#admin-reactions-list tbody").append(
        '<tr class="reaction[' + reaction.id +'] border-b">' +
            '<td class="px-4 py-3">' +
                '<div class="flex justify-center items-center">' +
                    '<img src="' + reaction.imagePath + '" class="w-10" alt="reaction image">' +
                '</div>' +
            '</td>' +
            '<td class="px-4 py-3">' +
                '<div class="flex justify-center items-center text-sm">' +
                    '<span>' + reaction.name + '</span>' +
                '</div>' +
            '</td>' +
            '<td class="px-4 py-2">' +
                '<div class="flex justify-center items-center">' +
                    '<button class="delete-reaction" type="button">' +
                        '<img src="/images/icons/close-icon.svg" class="w-8" alt="delete icon">' +
                    '</button>' +
                '</div>' +
            '</td>' +
        '</tr>'
    );
}