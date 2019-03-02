document.onLoad(){
    $('.matchedUser-link').on('click', function(e){
      e.preventDefault();
      $('#view-modal').modal('show').find('.modal-body').load($(this).attr('href') + ' #view');
    });
}

