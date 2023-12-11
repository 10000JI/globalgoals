/*!
* Start Bootstrap - Modern Business v5.0.7 (https://startbootstrap.com/template-overviews/modern-business)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-modern-business/blob/master/LICENSE)
*/
// This file is intentionally blank
// Use this file to add JavaScript to your project

const outModal = new bootstrap.Modal(document.getElementById('outModal'));
//회원탈퇴 모달
document.querySelector(".outSave").addEventListener('click', function () {
    fetch('/users/withdraw', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }
    })
        .then(function(response) {
            return response.text();
        })
        .then(function(result) {
            console.log("result: " + result);
            if (result === 'success') {
                alert("정상적으로 탈퇴되었습니다.");
                outModal.hide();
                location.href="/"
            }
        })
        .catch(function(error) {
            console.error('Fetch 오류:', error);
        });
})