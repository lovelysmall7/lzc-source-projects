//yuchuang 2017.5.12
    function iradio(){
        $('.hidden').siblings('label').find(':radio').attr('checked',true);
        $(".myradio input").click(function(e){
            var state = e.delegateTarget.defaultValue;
            var myradio = $(".myradio");
            var iclose = $(this).parents(".myradio").find('.close');
            // console.log(iclose);
            var iopen = $(this).parents(".myradio").find('.open');
            // console.log(state);
            $(this).parents(".myradio").find(':radio').removeAttr('checked');
            $(this).parent('label').addClass('disabled');
            $(this).parent('label').siblings('label').find(':radio').attr('checked',true);
            if (state == 'open') {
                open();
            } else {
                close();
            }
            

            function open(){
                iopen.animate({left:"50px"},100);
                setTimeout(function(){
                    iopen.hide();
                    iclose.show();
                    iopen.css('left',0);
                    $(".myradio label").removeClass('disabled');
                 },300);
            }

            function close(){
                iclose.animate({left:"0px"},100);
                setTimeout(function(){
                    iclose.hide();
                    iopen.show();
                    iclose.css('left','50px');
                    $(".myradio label").removeClass('disabled');
                 },300);
            }
        })
    }
        
