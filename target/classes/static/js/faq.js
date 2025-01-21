document.querySelectorAll('.post-tbody-tr2').forEach(function(tr){
    tr.addEventListener('click', function () {
        let index = tr.id.trim();
        window.location.href = '/faq/faqDetail?id=' + index;
    })
})