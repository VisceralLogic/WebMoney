<html>
<head>
	<title>View Account "<%= request.getParameter("account")%>"</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<script>
	function makeTransaction(date, ref, payee, category, memo, cleared, value){
		return "{\"date\":\"" + date + "\","
					+ "\"ref\":\"" + ref + "\","
					+ "\"payee\":\"" + payee + "\","
					+ "\"category\":\"" + category + "\","
					+ "\"memo\":\"" + memo + "\","
					+ "\"cleared\":" + cleared + ","
					+ "\"cents\":" + 100*value + "}";
	}
	
	function addTransaction(){
		var t = makeTransaction($("#date").val(), $("#ref").val(), $("#payee").val(),
			$("#category").val(), $("#memo").val(),
			($("#cleared").prop("checked") ? "true" : "false"),
			$("#cents").val());

		$.ajax({
			url: "account/<%= request.getParameter("account")%>/add",
			data: t,
			method: "POST",
			contentType: "application/json"
		})
		.done(function(){
			location.reload();
		})
		.fail(function(data, textStatus, jqXHR){
			alert("Could not add transaction\n" + jqXHR);
		});
	}

	function deleteTransaction(i){
		var tr = $("#delete" + i).closest("tr");
		var t = makeTransaction(tr.find(".date").text(), tr.find(".ref").text(), tr.find(".payee").text(),
			tr.find(".category").text(), tr.find(".memo").text(), tr.find(".cleared").text(),
			tr.find(".value").text());

		$.ajax({
			url: "account/<%= request.getParameter("account")%>/remove",
			data: t,
			method: "POST",
			contentType: "application/json"
		})
		.done(function(){
			location.reload();
		})
		.fail(function(data, textStatus, jqXHR){
			alert("Could not delete transaction\n" + jqXHR);	
		});
	}

	$(document).ready(function(){
		$.getJSON( "account/<%= request.getParameter("account")%>/transactions", function( data ) {
			var items = [];
			var i = 0;
			var account = "";
			var totalCents = 0;
			$.each( data, function( index, transaction ) {
				items.push("<tr><td class=\"date\">" + transaction.date + "</td>"
						+ "<td class=\"ref\">" + transaction.ref + "</td>"
						+ "<td class=\"payee\">" + transaction.payee + "</td>"
						+ "<td class=\"category\">" +
							(/^\[.+\]$/.test(transaction.category) ?
								(account = transaction.category.substr(1,transaction.category.length-2),
								"<a href=\"account.jsp?account=" + account + "\">" + account + "</a>")
							:
								transaction.category
							) + "</td>"
						+ "<td class=\"memo\">" + transaction.memo + "</td>"
						+ "<td class=\"cleared\">" + (transaction.cleared ? "true" : "false") + "</td>"
						+ "<td class=\"value\">" + (transaction.cents/100).toFixed(2) + "</td>"
						+ "<td><input type=\"button\" value=\"X\" id=\"delete" + i + "\" onClick=\"deleteTransaction(" + i + ");\"></td></tr>\n");
				i++;
				totalCents += transaction.cents;
			});
			$("#transactions").append(items.join(""));
			$("#total").html(
				totalCents > 0 ? "$" + (totalCents/100).toFixed(2) : "-$" + (totalCents/-100).toFixed(2)
			);
		});

		$.getJSON( "account/list", function( data ){
			var items = [];
			$.each( data, function( index, account ){
				$("#categories").append("<option value=\"[" + account + "]\">");
			});
		});
	});
	</script>
</head>
<body>
<table id="transactions">
<tr><td colspan="8"><h2>Account: <%= request.getParameter("account")%></h2></td></tr>
<tr><td colspan="8"><h3>Total: <div id="total"></div></h3></td></tr>
<tr>
	<th>Date</th>
	<th>Ref</th>
	<th>Payee</th>
	<th>Category</th>
	<th>Memo</th>
	<th>Cleared</th>
	<th>Amount ($)</th>
	<th></th>
</tr>
<tr>
	<td><input type="date" id="date"></td>
	<td><input type="text" id="ref"></td>
	<td><input type="text" id="payee"></td>
	<td><input type="text" id="category" list="categories">
		<datalist id="categories"></datalist>
	</td>
	<td><input type="text" id="memo"></td>
	<td><input type="checkbox" id="cleared"></td>
	<td><input type="number" id="cents"></td>
	<td><input type="button" value="Add" onClick="addTransaction();">
</tr>
<tr>
	<td colspan="8"><hr></td>
</tr>
</table>
</body>
</html>