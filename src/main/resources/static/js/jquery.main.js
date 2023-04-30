$(document).ready(function () {
    loadPosts();

    $("#create-post-upload-button").onclick = function () {
        $("#create-post-form input[type='file']").click();
    }

    $("#create-post-upload-button").on("click", function () {
        let fileDialog = $("#create-post-form input[type='file']");
        fileDialog.click();
        fileDialog.on("change", function () {
            let file = fileDialog.get(0).files[0];
            let reader = new FileReader();

            reader.onload = function () {
                $("#create-post-image-preview").attr("src", reader.result).show();
            }
            reader.readAsDataURL(file);
        })
    })

    $("#create-post-form").submit(function (e) {
       e.preventDefault();

        let frm = $(this);
        let data = new FormData($(this)[0])

       saveData(frm, data);
    });
})

function saveData(frm, data) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: frm.attr("action"),
        type: frm.attr("method"),
        data : data,
        dataType: "JSON",
        processData : false,
        contentType : false,
        success : function() {
            location.reload();
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
                    });

                    if (reactions == null) {
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
                    '<div class="flex justify-between p-2">' +
                        '<div class="mt-1 mr-2">' +
                            '<img src="' + post.creator.imagePath + '" class="w-8 rounded-xl mx-2 border" alt="user profile picture">' +
                        '</div>' +
                        '<div class="flex items-center justify-start text-sm w-full border rounded-xl">' +
                            '<textarea class="w-full bg-gray-100 border-r rounded-l-xl p-2 resize-y" rows="1" placeholder="Write something..."></textarea>' +
                            '<button type="button" class="mx-1">' +
                                '<img src="/images/icons/image-icon.svg" class="w-8" alt="image icon">' +
                            '</button>' +
                        '</div>' +
                        '<div class="flex items-center ml-1">' +
                            '<button type="button" class="px-2">' +
                                '<img src="/images/icons/arrow-right-icon.svg" class="w-10" alt="send icon">' +
                            '</button>' +
                        '</div>' +
                    '</div>' +
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