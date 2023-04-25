$(document).ready(function () {
    $("#image").change(function () {
        let file = $(this).get(0).files[0];

        if (file) {
            $("#image-preview").fadeTo("slow", 0, function () {
                let reader = new FileReader();

                reader.onload = function () {
                    $("#image-preview").attr("src", reader.result)
                        .fadeTo("slow", 1);
                }
                reader.readAsDataURL(file);
            });
        }
    });
});