window.addEventListener("DOMContentLoaded", function(){
    commonLib.loadEditor("description", 350)
        .then(editor => window.editor = editor);
});

/**
* 파일 업로드
*
*/
function callbackFileUpload(files) {
    console.log(files);
}