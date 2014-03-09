	var apikey = "8ac71d3fa777d7d7e9fbcdbacf3e513d";
	var baseURL= "http://api.musicgraph.com/api/v1/artist/search?";
	var artistInfoArray=[];


	function callApi(){



	  $(document).ready(function(){

		var query= document.getElementById("artist").value;
		  query.replace(" ","+");
	
		//First Call to Music Graph 
		  $.ajax({
			url: 'http://api.musicgraph.com/api/v1/artist/search?api_key=c26e63de67a52b71cfcb1b2fb63a14f2&influenced_by=' + query,
			success: searchCallBack
		  });
      });
    }
  function searchCallBack(data){
  	console.log("this is the first object");
  	console.log(data);
  	//artistInfoArray.push(data);

  	if(data.data.length == 0){
  		alert("sorry that artist is not in the database");

  	} else

  	//var count= 80;
    var query= document.getElementById("artist").value;

  	//MusicData is declaration of JSON object we will feed to d3 script
	var musicData= {"name": query, "value": 100, "children": []};
	//ChildArtistsDone is count of how many D3 Nodes have been Rendered
	var childArtistsDone = 0;

	//ChildArtist is the first 3 Results in an array form
	var childArtist=[];
	console.log("This these are the initial children of the main artist");
	//Take 4 artists from random locations in the array
	for (var x=0; x<4; x++){
		var n= Math.floor((Math.random()*data.data.length)+1);
		childArtist.push(data.data[n].name);
		//Function to delete duplicate artists in array
		childArtist= deleteDuplicates(childArtist);
		
	}
	
    
		//console.log(correct);
	for (var i in childArtist){
		if(childArtist[i]== childArtist[i++]){
		}
	}
	
	//.log(testArray);
	console.log(childArtist);

	//artistInfoArray is an array of every artist to be rendered
	artistInfoArray.push(data.data[0],data.data[1],data.data[2]);


		//Creating Entries in MusicData for each of the intial Results
		//Leave children empty and push with ajax data in apiCall()
  		for (var i in childArtist) {
  			childData = {
  				"name": childArtist[i],
  				"value": 75,
  				"children": []
  			};

	  		musicData.children.push(childData);
	  		//console.log(childData.name);
  			apiCall(childData);
		}
function apiCall(childData) {

	//Childdata is the object of each artist that needs children
	$.ajax({
		url: 'http://api.musicgraph.com/api/v1/artist/search?api_key=c26e63de67a52b71cfcb1b2fb63a14f2&influenced_by=' + encodeURIComponent(childData.name),
		success: function(data){
		  var count =0;
		 	
	//Check if data has children	
	      if(data.data.length >0){
		    for (var i in data) {

			  artistInfoArray.push(data.data[count]);
			  var bandName= data.data[count].name;
			
			childData.children.push({
				"name": data.data[count].name,
  				"value": 60,
  				"children": []
			});
			count++;
		    }
	      }

		childArtistsDone++;
		//Once all Artists have been entered into musicData Object
		if (childArtistsDone === childArtist.length) {
			render();

		}
	}
  });
}
  		console.log(musicData);

	
  function render(){
  	//Clear SVG canvas of previous visualizations
		clearCanvas();
		//Array of objects of artist info to be used later
		console.log(artistInfoArray);
	var canvas=d3.select("#display").append("svg")
		.attr("width",700)
		.attr("height",550)
		.append('g')
			.attr("transform","translate(50,50)");

	var tree= d3.layout.tree()
			.size([500,400])

		var nodes= tree.nodes(musicData);
		var links= tree.links(nodes);

		var node= canvas.selectAll(".node")
			.data(nodes)
			.enter()
			.append("g")
				.attr("class","node")
				.on("click",  moreInfo)
				.attr("transform", function(d){ return "translate(" + d.y + "," + d.x + ")";});

		node.append("circle")
			.attr("r",5)
			.attr("fill","#0078e7");

		node.append('text')
			.text(function(d){ return d.name;});

		var diagonal= d3.svg.diagonal()
				.projection(function(d){ return [d.y,d.x]; });	

		canvas.selectAll(".link")
			.data(links)
			.enter()
				.append("path")
				.attr("class","link")
				.attr("fill","none")
				.attr("stroke","#ADADAD")
				.attr("d",diagonal);	


    }
}

function moreInfo(d){

    //Diplsay Artist Information
   $("#info").empty().append("<p>" + d.name + "</p>");

}

function clearCanvas(){
    var canvas = d3.select("svg")
    .remove("node");
}

function deleteDuplicates(array){

	var sortedArray= array.sort();
	var results= [];

	var sortedArray= array.sort();
	for (var x=0; x< array.length; x++){
		if(sortedArray[x+1] != sortedArray[x]){
			results.push(sortedArray[x]);
		}
	}
	console.log(results);
	return results;
}
