// commonLib 객체가 이미 정의되어 있으면 그대로 사용하고, 그렇지 않으면 빈 객체로 초기화
// 이 코드는 commonLib 객체를 네임스페이스로 사용하여 관련 기능을 모아둠
var commonLib = commonLib ?? {};

/**
 * 메타 태그 정보 조회
 * mode - rootUrl : <meta name="rootUrl" ... /> 태그의 내용을 가져옴
 */
commonLib.getMeta = function(mode) {
    if (!mode) return; // mode 값이 없으면 함수를 종료

    // document.querySelector를 사용하여 지정된 name 속성 값을 가진 메타 태그를 찾음
    const el = document.querySelector(`meta[name='${mode}']`);

    // 찾은 태그의 content 속성 값을 반환합니다. 태그가 없으면 undefined를 반환
    return el?.content;
};

/**
 * 자바스크립트에서 만든 주소에 컨텍스트 경로 추가
 * @param url - 사용자가 입력한 URL
 * @returns - rootUrl에 사용자가 입력한 URL을 추가한 완전한 주소를 반환
 */
commonLib.url = function(url) {
    // commonLib.getMeta('rootUrl')에서 가져온 경로에서 앞의 "/"를 제거하고 입력받은 URL을 연결
    return `${commonLib.getMeta('rootUrl').replace("/", "")}${url}`;
};

/**
 * Ajax 요청 처리
 * @param url - 요청할 주소. 외부 URL이면 컨텍스트 경로를 추가하지 않음
 * @param method - HTTP 요청 방식 (기본값: 'GET').
 * @param callback - 응답 완료 후 실행할 콜백 함수.
 * @param data - 요청 본문 데이터 (POST, PATCH, PUT 시 사용).
 * @param headers - 추가 요청 헤더.
 * @param isText - true인 경우 텍스트로 응답을 처리
 * @returns - Promise 객체를 반환하여 비동기 작업을 처리
 */
commonLib.ajaxLoad = function(url, callback, method = 'GET', data, headers, isText = false) {
    if (!url) return; // URL이 없으면 종료.

    // CSRF 토큰과 헤더를 가져옵니다.
    const { getMeta } = commonLib;
    const csrfHeader = getMeta("_csrf_header");
    const csrfToken = getMeta("_csrf");

    // 외부 URL인지 확인하고 rootUrl을 추가
    url = /^http[s]?:/.test(url) ? url : commonLib.url(url);

    // 요청 헤더를 초기화하거나 기존 헤더에 CSRF 토큰을 추가
    headers = headers ?? {};
    headers[csrfHeader] = csrfToken;
    method = method.toUpperCase(); // HTTP 메서드를 대문자로 변환

    const options = { method, headers }; // fetch API 옵션 객체.

    // POST, PUT, PATCH 요청 시 요청 본문을 추가
    if (data && ['POST', 'PUT', 'PATCH'].includes(method)) { // body 쪽 데이터 추가 가능
        options.body = data instanceof FormData ? data : JSON.stringify(data);
    }

    return new Promise((resolve, reject) => {
        fetch(url, options)
            .then(res => {
                // 상태 코드가 204면 아무것도 반환하지 않음
                if (res.status !== 204)
                    return isText ? res.text() : res.json();
                else {
                    resolve();
                }
            })
            .then(json => {
                if (isText) { // 응답이 텍스트일 경우 바로 반환.
                    resolve(json);
                    return;
                }

                // 응답 데이터가 성공 상태인지 확인.
                if (json?.success) {
                    if (typeof callback === 'function') { // 콜백 함수 실행.
                        callback(json.data);
                    }
                    resolve(json);
                    return;
                }

                reject(json); // 처리 실패 시 거절.
            })
            .catch(err => {
                console.error(err); // 콘솔에 오류 출력.
                reject(err); // 응답 실패.
            });
    });
};

/**
 * 레이어 팝업 생성
 * @param url - 팝업에 표시할 콘텐츠 URL.
 * @param width - 팝업의 너비 (기본값: 350px).
 * @param height - 팝업의 높이 (기본값: 350px).
 * @param isAjax - true인 경우 Ajax로 콘텐츠를 로드
 */
commonLib.popup = function(url, width = 350, height = 350, isAjax = false) {
    // 기존 팝업과 레이어 요소를 제거
    const layerEls = document.querySelectorAll(".layer-dim, .layer-popup");
    layerEls.forEach(el => el.parentElement.removeChild(el));

    // 레이어 배경 요소 생성.
    const layerDim = document.createElement("div");
    layerDim.className = "layer-dim";

    // 레이어 팝업 요소 생성.
    const layerPopup = document.createElement("div");
    layerPopup.className = "layer-popup";

    // 팝업을 화면 가운데로 정렬.
    const xpos = (innerWidth - width) / 2;
    const ypos = (innerHeight - height) / 2;
    layerPopup.style.left = xpos + "px";
    layerPopup.style.top = ypos + "px";
    layerPopup.style.width = width + "px";
    layerPopup.style.height = height + "px";

    // 콘텐츠 영역 추가.
    const content = document.createElement("div");
    content.className = "layer-content";
    layerPopup.append(content);

    // 닫기 버튼 생성 및 추가.
    const button = document.createElement("button");
    const icon = document.createElement("i");
    button.className = "layer-close";
    button.type = "button";
    icon.className = "xi-close";
    button.append(icon);
    layerPopup.prepend(button);

    // 닫기 버튼 클릭 이벤트 등록.
    button.addEventListener("click", commonLib.popupClose);

    document.body.append(layerPopup);
    document.body.append(layerDim);

    // 팝업 콘텐츠 로드.
    if (isAjax) {
        const { ajaxLoad } = commonLib;
        ajaxLoad(url, null, 'GET', null, null, true)
            .then((text) => content.innerHTML = text);
    } else {
        // iframe으로 콘텐츠 로드.
        const iframe = document.createElement("iframe");
        iframe.width = width - 80;
        iframe.height = height - 80;
        iframe.frameBorder = 0;
        iframe.src = commonLib.url(url);
        content.append(iframe);
    }
};

/**
 * 레이어 팝업 제거
 */
commonLib.popupClose = function() {
    const layerEls = document.querySelectorAll(".layer-dim, .layer-popup");
    layerEls.forEach(el => el.parentElement.removeChild(el));
};

// DOMContentLoaded 이벤트에 추가 기능 등록.
window.addEventListener("DOMContentLoaded", function() {
    // 체크박스 전체 선택 토글.
    const checkAlls = document.getElementsByClassName("check-all");
    for (const el of checkAlls) {
        el.addEventListener("click", function() {
            const { targetClass } = this.dataset;
            if (!targetClass) return;

            const chks = document.getElementsByClassName(targetClass);
            for (const chk of chks) {
                chk.checked = this.checked;
            }
        });
    }

    // 팝업 버튼 클릭 이벤트 처리.
    const showPopups = document.getElementsByClassName("show-popup");
    for (const el of showPopups) {
        el.addEventListener("click", function() {
            const { url, width, height } = this.dataset;
            commonLib.popup(url, width, height);
        });
    }
});
