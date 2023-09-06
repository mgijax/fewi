<%@ attribute name="title" required="true" type="java.lang.String" description="Title of the page" %>
<%@ attribute name="userdoc" required="true" type="java.lang.String" description="User Help Page for the ?" %>

<div id="titleBarWrapper" userdoc="${userdoc}" style="max-width: none;">
   <div name="centeredTitle">
      <span class="titleBarMainTitle">
         ${title}
      </span>
   </div>
</div>