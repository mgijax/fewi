<style>
#resourceLinksWrapper{
  margin-top: 6px;
  margin-left:6px;
  width: 250px;
  height: 430px;
  border:2px solid;
  border-radius:22px;
  position:absolute;
  padding: 4px;
  background-color:#DFEFFF;
}
#hdpTourLink{
  position:absolute;
  top:10px;
  left:10px;
}
#hdpIntroLink{
  position:absolute;
  top:150px;
  left:10px;
}
#hdpGlossaryLink{
  position:absolute;
  top:290px;
  left:10px;
}
.relativePos { position:relative; width: 100%; height: 100%; }
</style>
<div id="resourceLinksWrapper" >
  <div class='relativePos'>

    <div id="hdpTourLink" >
      <a href="${configBean.MGIHOME_URL}other/hmdc_tour.shtml">
        <img src="${configBean.FEWI_URL}assets/images/static/hmdc_tour_button.png"
          style="" height="115" width="230">
        </a>
    </div>
    <div id="hdpIntroLink" >
      <a href="${configBean.MGIHOME_URL}other/homepage_IntroMouse.shtml">
        <img src="${configBean.FEWI_URL}assets/images/static/hmdc_intro_button.png"
          style="" height="115" width="230">
        </a>
    </div>
    <div id="hdpGlossaryLink" >
      <a href="${configBean.FEWI_URL}glossary">
        <img src="${configBean.FEWI_URL}assets/images/static/hmdc_glossary_button.png"
          style="" height="115" width="230">
        </a>
    </div>

  </div>
</div>
