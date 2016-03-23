<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<c:choose>
  <c:when test="${empty alleleSystems}">
    <span>MGI has not yet included tissue activity data for this allele in any anatomical systems.</span>
  </c:when>
  <c:otherwise>

<%
NotesTagConverter ntc = new NotesTagConverter(); 
%>

<style type="text/css">

  .stripe1 {background-color:#FFF;}
  .stripe2 {background-color:#EFEFEF;}
  .arrowRight,.arrowDown {float:right;}
  .leftBorder {border-left: solid 1px #ccc;}
  .noDisplay{display:none;}
  .noWrap {white-space: nowrap;}
  #alleleSystemtable_id { border-spacing:0px; border-collapse:collapse; border: 2px solid #AAA;}
  #alleleSystemtable_id td {text-align:center; padding: 4px 2px; border-left: solid 1px #ccc;}
  #alleleSystemtable_id th {text-align:center; padding: 4px 2px; border-left: solid 1px #ccc;}
  .systemRow { font-weight:bold;}
  .arrowRight,.arrowDown {float:right;}
  .hide {display:none;}
  .gridSystem {text-align:center; width:100%; font-weight:bold;}
  .gridSystemNeg {text-align:center; width:100%; font-weight:bold; line-height:60%; font-size:150%;}
  .gridStructure {text-align:center; width:100%;}
  .gridStructureNeg {text-align:center; width:100%; font-weight:bold; line-height:60%; font-size:150%}
  a.gridLink:link    {text-decoration:none; } 
  a.gridLink:visited {text-decoration:none; } 
  a.gridLink:hover   {text-decoration:underline; } 

td.border { border-bottom:thin solid grey; border-top:thin solid grey; border-left:thin solid grey; border-right:thin solid grey }
td.padLR { padding-left:4px; padding-right:4px }
td.padTop { padding-top:4px }
td.notBold { font-weight: normal; }
</style>


<!-- allele system/structure table -->
<table class="alleleSystemtable" id="alleleSystemtable_id">

<tr class="stripe1">
  <th> 
    <div style='text-align:left; font-size:120%; font-weight:bold; padding-bottom:5px;'>
      Activity in Systems/Structures 
    </div>
    <div style="text-align:left; padding-left:20px;" class="small">
      <A id='showRecomButton' style='cursor: pointer; color:blue;' CLASS='MP'>show</A> or
      <A id='hideRecomButton' style='cursor: pointer; color:blue;' CLASS='MP'>hide</A> all structures
    </div>
    <table width="!" border="0" CELLPADDING="1" CELLSPACING="1" bgcolor="#888888">
      <tr>
        <td bgcolor="#FFFFFF" class="border padLR small notBold" ALIGN="center" >&#8730;</td>
        <td bgcolor="#FFFFFF" class="border padLR small notBold" NOWRAP="nowrap">Activity Detected</td>
        <td class="border padLR" bgcolor="#FFFFFF" ALIGN="center" >
          <span style="font-weight:bold; font-size:140%; line-height:60%;">-</span>
        </td>
        <td bgcolor="#FFFFFF" class="border padLR small notBold" NOWRAP="nowrap">Activity Not Detected</td>
      </tr>
    </table>
  </th>
  <th>E 0-8.9</th>
  <th>E 9.0-13.9</th>
  <th>E 14-19.5</th>
  <th>P 0-21</th>
  <th>Post-weaning <br/><span class="small">P 22-42</span></th>
  <th>Adult<br/><span class="small">>P 43</span></th>
  <th>Images</th>
</tr>

<c:forEach var="alleleSystem" items="${alleleSystems}" varStatus="systemStatus">
  <tr class="systemRow ${systemStatus.index % 2==0 ? ' stripe2' : ' stripe1'}">

      <!-- system level row system -->
      <td  id="${alleleSystem.cssId}" class="noWrap borderUnder"  style="min-width:250px;" >
        <div style="text-align:left; cursor: pointer;">

		  <!-- Add the toggle arrows -->
          <span class="arrowRight ${alleleSystem.cssClass}">
	    <img src="${configBean.WEBSHARE_URL}images/rightArrow.gif"/>
          </span>
          <span style="display:none;" class="arrowDown ${alleleSystem.cssClass}">
	    <img src="${configBean.WEBSHARE_URL}images/downArrow.gif"/>
          </span>
        <span style='padding-left:5px;'>
          ${alleleSystem.system} 
        </span>

        </div>
      </td>

      <!-- system level row data -->
      <td  id="" class="noWrap borderUnder" style="min-width:60px" >
        <c:if test="${alleleSystem.ageE1 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystem'>&#8730;</div></a>
        </c:if>
        <c:if test="${alleleSystem.ageE1 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystemNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="min-width:60px" >
        <c:if test="${alleleSystem.ageE2 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystem'>&#8730;</div></a>
        </c:if>
        <c:if test="${alleleSystem.ageE2 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystemNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="min-width:60px" >
        <c:if test="${alleleSystem.ageE3 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystem'>&#8730;</div></a>
        </c:if>
        <c:if test="${alleleSystem.ageE3 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystemNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="min-width:60px" >
        <c:if test="${alleleSystem.ageP1 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystem'>&#8730;</div></a>
        </c:if>
        <c:if test="${alleleSystem.ageP1 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystemNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="min-width:60px" >
        <c:if test="${alleleSystem.ageP2 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystem'>&#8730;</div></a>
        </c:if>
        <c:if test="${alleleSystem.ageP2 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystemNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="min-width:60px" >
        <c:if test="${alleleSystem.ageP3 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystem'>&#8730;</div></a>
        </c:if>
        <c:if test="${alleleSystem.ageP3 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystemNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="min-width:50px" >
        <c:if test="${alleleSystem.hasImage == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridSystem'>&#8730;</div></a>
        </c:if>
      </td>

    </tr>

      <!-- structure level row  -->
      <c:forEach var="structure" items="${alleleSystem.recombinaseSystemStructures}" varStatus="structureStatus">
      <tr class="structureRow ${alleleSystem.cssClass} hide ${systemStatus.index % 2==0 ? ' stripe2' : ' stripe1'}">
      <td  id="" class="noWrap borderUnder" style="min-width:250px" >
        <div style="text-align:left; cursor: pointer;">

          <span style='padding-left:20px;'>
            ${structure.structure}
          </span>

        </div>
      </td>

      <!-- structure level row data -->
      <td  id="" class="noWrap borderUnder" style="" >
        <c:if test="${structure.ageE1 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructure'>&#8730;</div></a>
        </c:if>
        <c:if test="${structure.ageE1 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructureNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="" >
        <c:if test="${structure.ageE2 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructure'>&#8730;</div></a>
        </c:if>
        <c:if test="${structure.ageE2 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructureNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="" >
        <c:if test="${structure.ageE3 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructure'>&#8730;</div></a>
        </c:if>
        <c:if test="${structure.ageE3 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructureNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="" >
        <c:if test="${structure.ageP1 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructure'>&#8730;</div></a>
        </c:if>
        <c:if test="${structure.ageP1 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructureNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="" >
        <c:if test="${structure.ageP2 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructure'>&#8730;</div></a>
        </c:if>
        <c:if test="${structure.ageP2 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructureNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="" >
        <c:if test="${structure.ageP3 == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructure'>&#8730;</div></a>
        </c:if>
        <c:if test="${structure.ageP3 == '0' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructureNeg'>-</div></a>
        </c:if>
      </td>
      <td  id="" class="noWrap borderUnder" style="" >
        <c:if test="${structure.hasImage == '1' }">
          <a href='${configBean.FEWI_URL}recombinase/specificity?id=${allele.primaryID}&system=<fewi:encode value="${alleleSystem.system}"/>' 
          target='_blank' class='gridLink' ><div class='gridStructure'>&#8730;</div></a>
        </c:if>
      </td>
      </tr>
      </c:forEach>


  </tr>
</c:forEach>

</table>




<script type="text/javascript">

 $('#showRecomButton').click(function(){
   $('.structureRow').show();
   $('.arrowRight').hide();
   $('.arrowDown').show();
  });

 $('#hideRecomButton').click(function(){
   $('.structureRow').hide();
   $('.arrowDown').hide();
   $('.arrowRight').show();
  });

 // enable toggle on each table item
 <c:forEach var="alleleSystem" items="${alleleSystems}" >
  $('#${alleleSystem.cssId}','#alleleSystemtable_id').click(function(){
    $('.${alleleSystem.cssClass}','#alleleSystemtable_id').toggle();
  });
 </c:forEach>

</script>


  </c:otherwise>
</c:choose>


