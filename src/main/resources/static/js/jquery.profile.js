$(document).ready(function () {
    $("#add-to-friends").click(function () {
        let id = $(this).val();
        inviteUser(id);
    });
});

function inviteUser(id) {
    $.ajax({
        enctype : 'multipart/form-data',
        url: "/api/users/invite/" + id,
        type: "PUT",
        dataType: "JSON",
        processData : false,
        contentType : false,
        success : function() {
            location.reload();
        }
    });
}