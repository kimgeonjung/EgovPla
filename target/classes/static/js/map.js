const draggableList = document.getElementById('draggable-checkbox-list'); // 드래그 가능한 리스트 요소 가져오기
let draggedItem = null; // 드래그 중인 항목
let placeholder = null; // 플레이스홀더 요소
let layer_list = [];
const categoryButton = document.querySelectorAll('#category-button button');
const submenuList = document.querySelectorAll('#submenu-list li');

// 각 버튼에 클릭 이벤트 추가
categoryButton.forEach((button) => {
    button.addEventListener('click', () => {
        const category = button.id.replace('category-', '');
        if (category === 'all') {
            // "전체" 버튼 클릭 시 모든 li 표시
            submenuList.forEach((li) => li.classList.remove('hidden'));
        } else {
            // 모든 li 숨기기
            submenuList.forEach((li) => {
                if (li.id === category) {
                    li.classList.remove('hidden'); // 선택된 카테고리는 표시
                } else {
                    li.classList.add('hidden'); // 나머지는 숨기기
                }
            });
        }
    });
});

// 드래그 앤 드롭 이벤트 처리
draggableList.addEventListener('dragstart', (e) => {
    if (e.target.tagName === 'LI') { // 드래그가 LI 요소에서 시작될 때만 실행
        draggedItem = e.target; // 드래그 중인 항목 저장
        draggedItem.classList.add('dragging'); // 드래그 중인 항목에 스타일 추가
        placeholder = document.createElement('li'); // 플레이스홀더 생성
        placeholder.className = 'placeholder'; // 플레이스홀더 스타일 클래스 추가
        e.target.parentNode.insertBefore(placeholder, e.target.nextSibling); // 플레이스홀더를 드래그 항목 아래에 추가
    }
});

draggableList.addEventListener('dragover', (e) => {
    e.preventDefault(); // 기본 동작 방지
    const draggingOverItem = e.target.closest('li'); // 드래그 중인 항목 탐지
    if (draggingOverItem && draggingOverItem !== placeholder && draggingOverItem !== draggedItem) { // 유효한 항목일 경우
        const bounding = draggingOverItem.getBoundingClientRect(); // 항목의 위치 정보 가져오기
        const offset = e.clientY - bounding.top; // 마우스 위치 계산
        if (offset > bounding.height / 2) { // 마우스가 항목의 하단에 위치한 경우
            draggingOverItem.after(placeholder); // 플레이스홀더를 항목 뒤로 이동
        } else { // 상단에 위치한 경우
            draggingOverItem.before(placeholder); // 플레이스홀더를 항목 앞으로 이동
        }
    }
});

const fullscreen = document.getElementById('fullscreenButton');
const map = document.getElementById('map');

fullscreen.addEventListener('click', () => {
    if (map.requestFullscreen) {
        map.requestFullscreen(); // 표준 API
    } else if (map.webkitRequestFullscreen) {
        map.webkitRequestFullscreen(); // Safari
    } else if (map.msRequestFullscreen) {
        map.msRequestFullscreen(); // IE/Edge
    }
});

const layer_invisible = document.getElementById('layer-invisible');
const check_layer = document.getElementById('check-layer');
const layer_cancel = document.getElementById('layer-cancel');

layer_cancel.addEventListener('click', ()=>{
    if (check_layer.style.display === 'block' || check_layer.style.display === '') {
        check_layer.style.display = 'none';
    }
});

layer_invisible.addEventListener('click', () => {
    if (check_layer.style.display === 'block' || check_layer.style.display === '') {
        check_layer.style.display = 'none'; // 안 보이게 설정
    }
    else if (check_layer.style.display === 'none') {
        check_layer.style.display = 'block'; // 보이게 설정

    }
});

const layer_names = [
    { name: '광주 동', id: 'gwangju_dong' },
    { name: '광주 구', id: 'gwangju_gu' },
    { name: '초등학생', id: 'gwangju_population_10c' },
    { name: '중학생', id: 'gwangju_population_10j' },
    { name: '고등학생', id: 'gwangju_population_10g' },
    { name: '20대', id: 'gwangju_population_20y' },
    { name: '30대', id: 'gwangju_population_30y' },
    { name: '40대', id: 'gwangju_population_40y' },
    { name: '50대', id: 'gwangju_population_50y' },
    { name: '60대', id: 'gwangju_population_60y' },
    { name: '70대', id: 'gwangju_population_70y' },
    { name: '80대', id: 'gwangju_population_80y' },
    { name: '90대', id: 'gwangju_population_90y' },
    { name: '모든 연령대', id: 'gwangju_population_all' },
    { name: '경지정리답', id: 'gwangju_toji_use_1110' },
    { name: '미경지정리답', id: 'gwangju_toji_use_1120' },
    { name: '보통,특수작물', id: 'gwangju_toji_use_1210' },
    { name: '과수원 기타', id: 'gwangju_toji_use_1220' },
    { name: '자연초지', id: 'gwangju_toji_use_2110' },
    { name: '인공초지', id: 'gwangju_toji_use_2120' },
    { name: '침엽수림', id: 'gwangju_toji_use_2210' },
    { name: '활엽수림', id: 'gwangju_toji_use_2220' },
    { name: '혼합수림', id: 'gwangju_toji_use_2230' },
    { name: '골프장', id: 'gwangju_toji_use_2310' },
    { name: '공원묘지', id: 'gwangju_toji_use_2320' },
    { name: '유원지', id: 'gwangju_toji_use_2330' },
    { name: '일반주택지', id: 'gwangju_toji_use_3110' },
    { name: '고층주택지', id: 'gwangju_toji_use_3120' },
    { name: '상업,업무지', id: 'gwangju_toji_use_3130' },
    { name: '나대지 및 인공녹지', id: 'gwangju_toji_use_3140' },
    { name: '도로', id: 'gwangju_toji_use_3210' },
    { name: '철로 및 주변지역', id: 'gwangju_toji_use_3220' },
    { name: '공항', id: 'gwangju_toji_use_3230' },
    { name: '공업시설', id: 'gwangju_toji_use_3310' },
    { name: '공업나지, 기타', id: 'gwangju_toji_use_3320' },
    { name: '발전시설', id: 'gwangju_toji_use_3410' },
    { name: '처리장', id: 'gwangju_toji_use_3420' },
    { name: '교육, 군사시설', id: 'gwangju_toji_use_3430' },
    { name: '공공용지', id: 'gwangju_toji_use_3440' },
    { name: '양어장, 양식장', id: 'gwangju_toji_use_3510' },
    { name: '채광지역', id: 'gwangju_toji_use_3520' },
    { name: '매립지', id: 'gwangju_toji_use_3530' },
    { name: '가축사육시설', id: 'gwangju_toji_use_3550' },
    { name: '하천', id: 'gwangju_toji_use_4210' },
    { name: '호, 소', id: 'gwangju_toji_use_4310' },
    { name: '댐', id: 'gwangju_toji_use_4320' },
    { name: '일본인, 창씨명등', id: 'gwangju_toji_owner_00' },
    { name: '개인', id: 'gwangju_toji_owner_01' },
    { name: '국유지', id: 'gwangju_toji_owner_02' },
    { name: '외국인, 외국공공기관', id: 'gwangju_toji_owner_03' },
    { name: '시, 도유지', id: 'gwangju_toji_owner_04' },
    { name: '군유지', id: 'gwangju_toji_owner_05' },
    { name: '법인', id: 'gwangju_toji_owner_06' },
    { name: '종증', id: 'gwangju_toji_owner_07' },
    { name: '종교단체', id: 'gwangju_toji_owner_08' },
    { name: '기타', id: 'gwangju_toji_owner_09' },
    { name: '도서관', id: 'gwangju_library' },
    { name: '민원발급기', id: 'gwangju_civil_service_machines' },
    { name: '천안 구', id: 'cheonan_si'}
];

// 레이어의 인덱스와 체크박스의 ID를 배열로 정의
const layers = [
    { id: 'gwangju_dong', layerIndex: 1 },
    { id: 'gwangju_gu', layerIndex: 2 },
    { id: 'gwangju_population_10c', layerIndex: 3 },
    { id: 'gwangju_population_10j', layerIndex: 4 },
    { id: 'gwangju_population_10g', layerIndex: 5 },
    { id: 'gwangju_population_20y', layerIndex: 6 },
    { id: 'gwangju_population_30y', layerIndex: 7 },
    { id: 'gwangju_population_40y', layerIndex: 8 },
    { id: 'gwangju_population_50y', layerIndex: 9 },
    { id: 'gwangju_population_60y', layerIndex: 10 },
    { id: 'gwangju_population_70y', layerIndex: 11 },
    { id: 'gwangju_population_80y', layerIndex: 12 },
    { id: 'gwangju_population_90y', layerIndex: 13 },
    { id: 'gwangju_population_all', layerIndex: 14 },
    { id: 'gwangju_toji_use_1110', layerIndex: 15 },
    { id: 'gwangju_toji_use_1120', layerIndex: 16 },
    { id: 'gwangju_toji_use_1210', layerIndex: 17 },
    { id: 'gwangju_toji_use_1220', layerIndex: 18 },
    { id: 'gwangju_toji_use_2110', layerIndex: 19 },
    { id: 'gwangju_toji_use_2120', layerIndex: 20 },
    { id: 'gwangju_toji_use_2210', layerIndex: 21 },
    { id: 'gwangju_toji_use_2220', layerIndex: 22 },
    { id: 'gwangju_toji_use_2230', layerIndex: 23 },
    { id: 'gwangju_toji_use_2310', layerIndex: 24 },
    { id: 'gwangju_toji_use_2320', layerIndex: 25 },
    { id: 'gwangju_toji_use_2330', layerIndex: 26 },
    { id: 'gwangju_toji_use_3110', layerIndex: 27 },
    { id: 'gwangju_toji_use_3120', layerIndex: 28 },
    { id: 'gwangju_toji_use_3130', layerIndex: 29 },
    { id: 'gwangju_toji_use_3140', layerIndex: 30 },
    { id: 'gwangju_toji_use_3210', layerIndex: 31 },
    { id: 'gwangju_toji_use_3220', layerIndex: 32 },
    { id: 'gwangju_toji_use_3230', layerIndex: 33 },
    { id: 'gwangju_toji_use_3310', layerIndex: 34 },
    { id: 'gwangju_toji_use_3320', layerIndex: 35 },
    { id: 'gwangju_toji_use_3410', layerIndex: 36 },
    { id: 'gwangju_toji_use_3420', layerIndex: 37 },
    { id: 'gwangju_toji_use_3430', layerIndex: 38 },
    { id: 'gwangju_toji_use_3440', layerIndex: 39 },
    { id: 'gwangju_toji_use_3510', layerIndex: 40 },
    { id: 'gwangju_toji_use_3520', layerIndex: 41 },
    { id: 'gwangju_toji_use_3530', layerIndex: 42 },
    { id: 'gwangju_toji_use_3550', layerIndex: 43 },
    { id: 'gwangju_toji_use_4210', layerIndex: 44 },
    { id: 'gwangju_toji_use_4310', layerIndex: 45 },
    { id: 'gwangju_toji_use_4320', layerIndex: 46 },
    { id: 'gwangju_toji_owner_00', layerIndex: 47 },
    { id: 'gwangju_toji_owner_01', layerIndex: 48 },
    { id: 'gwangju_toji_owner_02', layerIndex: 49 },
    { id: 'gwangju_toji_owner_03', layerIndex: 50 },
    { id: 'gwangju_toji_owner_04', layerIndex: 51 },
    { id: 'gwangju_toji_owner_05', layerIndex: 52 },
    { id: 'gwangju_toji_owner_06', layerIndex: 53 },
    { id: 'gwangju_toji_owner_07', layerIndex: 54 },
    { id: 'gwangju_toji_owner_08', layerIndex: 55 },
    { id: 'gwangju_toji_owner_09', layerIndex: 56 },
    { id: 'gwangju_library', layerIndex: 57 },
    { id: 'gwangju_civil_service_machines', layerIndex: 58 },
    { id: 'cheonan_si', layerIndex: 59}
];

document.addEventListener('DOMContentLoaded', function() {
    const regions = {
        gwangju: { center: [126.8526, 35.1595], zoom: 11 },
        cheonan: { center: [127.1523, 36.8154], zoom: 12 }
    };
    // 광주 메뉴 보이기
    document.getElementById('submenu-list').style.display = 'block';

    // 천안 메뉴 숨기기
    document.getElementById('submenu-list2').style.display = 'none';

    // 광주 버튼 클릭 시
    document.getElementById('btn-gwangju').addEventListener('click', function() {
        const view = map.getView();
        view.setCenter(regions.gwangju.center); // 광주 중심으로 이동
        view.setZoom(regions.gwangju.zoom);    // 광주 줌 레벨 설정


        // 광주 메뉴 보이기
        document.getElementById('submenu-list').style.display = 'block';

        // 천안 메뉴 숨기기
        document.getElementById('submenu-list2').style.display = 'none';

    });

    // 천안 버튼 클릭 시
    document.getElementById('btn-cheonan').addEventListener('click', function() {
        const view = map.getView();
        view.setCenter(regions.cheonan.center); // 천안 중심으로 이동
        view.setZoom(regions.cheonan.zoom);    // 천안 줌 레벨 설정



        // 천안 메뉴 보이기
        document.getElementById('submenu-list2').style.display = 'block';

        // 광주 메뉴 숨기기
        document.getElementById('submenu-list').style.display = 'none';
    });
    // OpenLayers Map 객체 생성
    var map = new ol.Map({
        target: 'map', // map div에 지도 표시
        layers: [
            new ol.layer.Tile({
                source: new ol.source.OSM()  // OpenStreetMap 배경 지도
            }), // 0번
            createWmsLayer('ne:gwangju_dong', false), // 1번 광주 전체
            createWmsLayer('ne:gwangju_gu', false), // 2번 광주 구
            createWmsLayer('ne:gwangju_population_10c', false), // 3번 초등학생
            createWmsLayer('ne:gwangju_population_10j', false), // 4번 중학생
            createWmsLayer('ne:gwangju_population_10g', false), // 5번 고등학생
            createWmsLayer('ne:gwangju_population_20y', false), // 6번 20대
            createWmsLayer('ne:gwangju_population_30y', false), // 7번 30대
            createWmsLayer('ne:gwangju_population_40y', false), // 8번 40대
            createWmsLayer('ne:gwangju_population_50y', false), // 9번 50대
            createWmsLayer('ne:gwangju_population_60y', false), // 10번 60대
            createWmsLayer('ne:gwangju_population_70y', false), // 11번 70대
            createWmsLayer('ne:gwangju_population_80y', false), // 12번 80대
            createWmsLayer('ne:gwangju_population_90y', false), // 13번 90대
            createWmsLayer('ne:gwangju_population_all', false), // 14번 모든 연령대
            createWmsLayer('ne:gwangju_toji_use_1110', false), // 15번 경지정리답
            createWmsLayer('ne:gwangju_toji_use_1120', false), // 16번 미경지정리답
            createWmsLayer('ne:gwangju_toji_use_1210', false), // 17번 보통,특수작물
            createWmsLayer('ne:gwangju_toji_use_1220', false), // 18번 과수원 기타
            createWmsLayer('ne:gwangju_toji_use_2110', false), // 19번 자연초지
            createWmsLayer('ne:gwangju_toji_use_2120', false), // 20번 인공초지
            createWmsLayer('ne:gwangju_toji_use_2210', false), // 21번 침엽수림
            createWmsLayer('ne:gwangju_toji_use_2220', false), // 22번 활엽수림
            createWmsLayer('ne:gwangju_toji_use_2230', false), // 23번 혼합수림
            createWmsLayer('ne:gwangju_toji_use_2310', false), // 24번 골프장
            createWmsLayer('ne:gwangju_toji_use_2320', false), // 25번 공원묘지
            createWmsLayer('ne:gwangju_toji_use_2330', false), // 26번 유원지
            createWmsLayer('ne:gwangju_toji_use_3110', false), // 27번 일반주택지
            createWmsLayer('ne:gwangju_toji_use_3120', false), // 28번 고층주택지
            createWmsLayer('ne:gwangju_toji_use_3130', false), // 29번 상업,업무지
            createWmsLayer('ne:gwangju_toji_use_3140', false), // 30번 나대지 및 인공녹지
            createWmsLayer('ne:gwangju_toji_use_3210', false), // 31번 도로
            createWmsLayer('ne:gwangju_toji_use_3220', false), // 32번 철로 및 주변지역
            createWmsLayer('ne:gwangju_toji_use_3230', false), // 33번 공항
            createWmsLayer('ne:gwangju_toji_use_3310', false), // 34번 공업시설
            createWmsLayer('ne:gwangju_toji_use_3320', false), // 35번 공업나지, 기타
            createWmsLayer('ne:gwangju_toji_use_3410', false), // 36번 발전시설
            createWmsLayer('ne:gwangju_toji_use_3420', false), // 37번 처리장
            createWmsLayer('ne:gwangju_toji_use_3430', false), // 38번 교육, 군사시설
            createWmsLayer('ne:gwangju_toji_use_3440', false), // 39번 공공용지
            createWmsLayer('ne:gwangju_toji_use_3510', false), // 40번 양어장, 양식장
            createWmsLayer('ne:gwangju_toji_use_3520', false), // 41번 채광지역
            createWmsLayer('ne:gwangju_toji_use_3530', false), // 42번 매립지
            createWmsLayer('ne:gwangju_toji_use_3550', false), // 43번 가축사육시설
            createWmsLayer('ne:gwangju_toji_use_4210', false), // 44번 하천
            createWmsLayer('ne:gwangju_toji_use_4310', false), // 45번 호, 소
            createWmsLayer('ne:gwangju_toji_use_4320', false), // 46번 댐
            createWmsLayer('ne:gwangju_toji_owner_00', false), // 47번 일본인, 창씨명등
            createWmsLayer('ne:gwangju_toji_owner_01', false), // 48번 개인
            createWmsLayer('ne:gwangju_toji_owner_02', false), // 49번 국유지
            createWmsLayer('ne:gwangju_toji_owner_03', false), // 50번 외국인, 외국공공기간
            createWmsLayer('ne:gwangju_toji_owner_04', false), // 51번 시, 도유지
            createWmsLayer('ne:gwangju_toji_owner_05', false), // 52번 군유지
            createWmsLayer('ne:gwangju_toji_owner_06', false), // 53번 법인
            createWmsLayer('ne:gwangju_toji_owner_07', false), // 54번 종증
            createWmsLayer('ne:gwangju_toji_owner_08', false), // 55번 종교단체
            createWmsLayer('ne:gwangju_toji_owner_09', false), // 56번 기타
            createWmsLayer('ne:gwangju_library', false), // 57번 도서관
            createWmsLayer('ne:gwangju_civil_service_machines', false), // 58번 민원발급기
            createWmsLayer('ne:cheonan_si', false) // 59번 천안 구

        ],
        view: new ol.View({
            projection: 'EPSG:4326',  // 지도 좌표계 설정
            center: [126.8526, 35.1595],  // 광주의 경도, 위도 좌표
            zoom: 11,  // 기본 줌 레벨
            minZoom: 1, // 최소 줌 레벨 설정
            maxZoom: 15, // 최대 줌 레벨 설정
        })
    });

    // 레이어우선순위에 대해 이벤트 리스너 추가
    layers.forEach(function(layerInfo) {
        var checkbox = document.getElementById(layerInfo.id);
        checkbox.addEventListener('change', function(event) {
            if (event.target.checked) {

                var wmsLayer = map.getLayers().item(layerInfo.layerIndex); // WMS 레이어
                wmsLayer.setVisible(true);  // 체크된 상태에 따라 레이어 표시

                // 'visible'이 true인 레이어만 필터링하여 출력
                const visibleLayers = map.getLayers().getArray().filter(layer => layer.getVisible());

                // 레이어 우선순위에 추가
                // ul 요소 선택
                const ul = document.getElementById("draggable-checkbox-list");
                // 새로운 li 요소 생성
                const newLi = document.createElement("li");
                newLi.setAttribute("draggable", "true");
                newLi.setAttribute("id", layerInfo.id+'-layer');

                const newBtn = document.createElement("button");
                newBtn.setAttribute("id", layerInfo.id);
                newBtn.setAttribute("class", "cancel-layer");

                // 새로운 img 생성
                const newDragImg = document.createElement("img");
                newDragImg.setAttribute("src", "/images/icons8-메뉴.svg");
                newDragImg.setAttribute("style", "width: 20px; height: 20px");
                newDragImg.setAttribute("draggable", "false");

                // 새로운 label 생성
                const newLabel = document.createElement("label");
                newLabel.setAttribute("for", layerInfo.id+'-label');

                var findName = layer_names.find(layer => layer.id === layerInfo.id);
                // li 태그 텍스트
                newLabel.textContent = findName.name;

                // 새로운 img 생성
                const newCancelImg = document.createElement("img");
                newCancelImg.setAttribute("src", "/images/icons8-취소.svg");
                newCancelImg.setAttribute("style", "width: 20px; height: 20px");
                newCancelImg.setAttribute("draggable", "false");

                newBtn.appendChild(newCancelImg);

                // li에 checkbox, label, img 추가
                newLi.appendChild(newDragImg);
                newLi.appendChild(newLabel);
                newLi.appendChild(newBtn);

                // ul에 li 추가
                ul.appendChild(newLi);


                // cancel 버튼 클릭 이벤트 추가
                newBtn.addEventListener('click', function() {
                    var searchBox = document.getElementById('search-'+layerInfo.id);
                    // li 태그 삭제
                    newLi.remove();
                    // 체크박스 해제
                    checkbox.checked = false;
                    if (searchBox !== null){
                        searchBox.checked = false;
                    }
                    var wmsLayer = map.getLayers().item(layerInfo.layerIndex); // WMS 레이어
                    wmsLayer.setVisible(checkbox.checked);  // 체크된 상태에 따라 레이어 표시/숨기기

                    // 레이어 숨기기
                    var wmsLayer = map.getLayers().item(layerInfo.layerIndex); // WMS 레이어
                    wmsLayer.setVisible(false);  // 체크 해제된 상태에 따라 레이어 숨기기

                    // 'visible'이 true인 레이어만 필터링하여 출력
                    const visibleLayers = map.getLayers().getArray().filter(layer => layer.getVisible());

                });
            } else {
                const div_layer = document.querySelector(`#${layerInfo.id}-layer`);
                if (div_layer)
                    div_layer.remove();

            }
        });
    });

    draggableList.addEventListener('dragend', () => {
        if (draggedItem) { // 드래그 항목이 있을 경우
            draggedItem.classList.remove('dragging'); // 드래그 중 스타일 제거
            if (placeholder) { // 플레이스홀더가 존재할 경우
                placeholder.parentNode.replaceChild(draggedItem, placeholder); // 플레이스홀더를 드래그 항목으로 교체
                placeholder = null; // 플레이스홀더 제거
            }
            draggedItem = null; // 드래그 항목 초기화

            const items = Array.from(draggableList.querySelectorAll('li')); // 모든 li 요소를 배열로 가져오기
            layer_list = []; // 배열 초기화 (이전에 저장된 값들을 제거)

            items.forEach((item, index) => {
                const label = item.querySelector('label'); // li 안의 label 요소 선택
                const labelText = label.textContent.trim(); // label 텍스트 가져오기
                const id = item.id.slice(0, -6); // ID에서 _layer 부분 제거
                layer_list.push({ index: 1000 - (index * 50), id: "ne:" + id, labelText: labelText });
            });

            // 현재 맵에 존재하는 레이어를 순회하면서 id가 일치하는 레이어의 zIndex를 수정
            map.getLayers().getArray().forEach(layer => {
                const layerId = layer.get('id');
                const matchingLayer = layer_list.find(item => item.id === layerId);

                if (matchingLayer) {
                    // 해당 레이어가 layer_list에 존재하면 zIndex 값을 수정
                    layer.setZIndex(matchingLayer.index);
                }
            });
        }
    });





    // 왼쪽 체크박스에 대해 이벤트 리스너 추가
    layers.forEach(function(layerInfo) {
        var checkbox = document.getElementById(layerInfo.id);
        checkbox.addEventListener('change', function() {
            var wmsLayer = map.getLayers().item(layerInfo.layerIndex); // WMS 레이어
            wmsLayer.setVisible(checkbox.checked);  // 체크된 상태에 따라 레이어 표시/숨기기
        });
    });

    // WMS 레이어 생성 함수
    function createWmsLayer(layerName, visible) {
        const layer = new ol.layer.Tile({
            source: new ol.source.TileWMS({
                url: 'http://localhost:8080/geoserver/ne/wms',  // GeoServer WMS URL
                params: {
                    'LAYERS': layerName,  // 사용하려는 레이어 이름
                    'TILED': true,
                    'FORMAT': 'image/png',
                    'SRS': 'EPSG:4326'  // 좌표계 설정
                },
                serverType: 'geoserver'
            }),
            visible: visible  // 기본적으로 레이어 가시성 설정
        });
        layer.set('id', layerName);  // layerName을 id로 설정
        return layer;
    }
    // 지도 클릭시
    map.on('click', function (event) {
        var coordinate = event.coordinate;
        var view = map.getView();
        var resolution = view.getResolution();
        var projection = view.getProjection();

        // 클릭한 위치 좌표
        var lonLatText = '클릭한 위치의 위도 : ' +  coordinate[1].toFixed(6) + ', 경도 : ' + coordinate[0].toFixed(6); // 위도, 경도

        // 클릭한 위치 이름
        var url1 = map.getLayers().item(1).getSource().getGetFeatureInfoUrl(coordinate, resolution, projection, {
            'INFO_FORMAT': 'application/json'
        });

        // 데이터 요청
        fetch(url1)
            .then(response => response.json())  // JSON 형식으로 응답을 처리
            .then(data => {
                const properties = data.features.map(feature => feature.properties);
                const name = data.features.map(feature => feature.properties.adm_nm);
            })
            .catch(error => {
                console.error('Error fetching data:', error);  // 에러 처리
            });
    });
});

// 메뉴 열고 닫기 토글 기능
function toggleMenu() {
    const menu = document.getElementById('menu');
    const menuButton = document.getElementById('menu-button');
    menu.classList.toggle('hidden');
    menuButton.classList.toggle('hidden');
}

// 서브메뉴 열고 닫기 토글 기능
function toggleSubmenu(event, element) {
    event.preventDefault();
    const listItem = element.parentElement;
    const submenu = listItem.querySelector('.submenu');
    if (submenu) {
        submenu.classList.toggle('active');
    }
}

// 검색 입력 필드에 이벤트 리스너 추가
document.getElementById('layer-search').addEventListener('input', filterList);

function filterList() {
    const keyword = document.getElementById('layer-search').value.trim().toLowerCase();
    const searchList = document.getElementById('search-list');

    if (keyword === '') {
        searchList.style.display = 'none'; // 검색어가 없으면 숨기기
        searchList.innerHTML = ''; // 리스트 초기화
        return;
    }

    searchList.style.display = 'block'; // 검색어가 있으면 표시
    searchList.innerHTML = ''; // 기존 검색 결과 초기화

    // 입력된 키워드와 일치하는 항목 필터링
    const filteredItems = layer_names.filter(item => item.name.toLowerCase().includes(keyword));

    filteredItems.forEach(item => {
        const listItem = document.createElement('li');
        listItem.classList.add('check-item'); // li에 클래스 추가

        const span = document.createElement('span');
        span.classList.add('submenu-item');
        span.textContent = item.name; // 레이어 이름 표시

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.value = item.id;
        checkbox.id = 'search-' + item.id;
        checkbox.checked = document.getElementById(item.id)?.checked || false; // 기존 체크박스 상태 동기화

        listItem.appendChild(span);
        listItem.appendChild(checkbox);
        searchList.appendChild(listItem);

        // 기존 체크박스와 동기화
        checkbox.addEventListener('change', function (event) {
            const originalCheckbox = document.getElementById(item.id);
            originalCheckbox.checked = event.target.checked; // 원래 체크박스 상태 동기화

            // 기존 이벤트 트리거
            originalCheckbox.dispatchEvent(new Event('change'));
        });
    });
}