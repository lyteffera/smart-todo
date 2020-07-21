let prevQuoteNum = 0;
function addHelp() {
const greeting =
      ['To use the Smart To-Do, you must be logged into your Google Account. Once you are logged in, you will be able to add and delete tasks, which will automatically be scheduled onto your calendar. Try it!'];
       
      var newGreetingNum = Math.floor(Math.random() * greeting.length);

    if (newGreetingNum == prevQuoteNum) {
        ++newGreetingNum;
        newGreetingNum %= greeting.length;
    }
    prevQuoteNum = newGreetingNum;
    alert(greeting[newGreetingNum]);
}

function googleTranslateElementInit() {
  new google.translate.TranslateElement({pageLanguage: 'en', layout: google.translate.TranslateElement.InlineLayout.SIMPLE}, 'google_translate_element');
}


async function getDataText(path) {
  // The fetch() function returns response object, and is asynchronous.
  const response = await fetch(path);
  //the text() function gets the text from the response asynchronously
  const data = await response.text();
  // When the request is complete return text from response
  return data;
}
async function getDataJson(path) {
  // The fetch() function returns response object, and is asynchronous.
  const response = await fetch(path);
  //the text() function gets the text from the response asynchronously
  const data = await response.json();
  // When the request is complete return text from response
  return data;
}
async function include(path,extension){
    const element = document.getElementById(`include-${path}`);
    if(element)
    {
        const header = await getDataText(`/${path}${extension}`);
        element.innerHTML = header+'\n'+element.innerHTML;
    }
}
async function includeHTML(fileName){
    include(fileName,".html");
}
async function includeDynamicHTML(fileName){
    include(fileName,"");
}
function jsListToHtml(list){
    let html = '<ul style="list-style-type:none;">';
    for(element of list)
    {
      const checked = element.status==="closed"?'checked':"";
      html += `<li><input type="checkbox" id="${element.id}" name="${element.id}" value="${element.message}" ${checked} 
                onclick="updateRecordStatus(event.target)"/>
      <label for="${element.id}">${element.message}</label>
      <input type="submit" class="button" value="Delete" name="id" onclick = "deleteObject('${element.id}')"/></li>`;

    }
    html += '</ul>';
    return html
}
async function updateDataInclude(){
    const element = document.getElementById(`include-data`);
    if(element)
    {
        const data = await getDataJson(`/data`);
        element.innerHTML = jsListToHtml(data)+'\n'+element.innerHTML;
    }
}

function updateRecordStatus(entity){
    let data = {id: entity.name};

    fetch("/todo_updates", {
        method: "POST", 
        body: JSON.stringify(data)
    }).then(()=>{
        const dropDown = document.getElementById('dependency_drop_down');
        dropDown.innerHTML = '';
        addItemsToDropDown('dependency_drop_down');
    });
    
}
function deleteObject(id){
    let data = [{id:id}];
    fetch("/delete", {
        method: "POST", 
        body: JSON.stringify(data)
    }).then(result => {
        location.href = "index.html";
    });
}
function deleteCompleted(){
    let data = [];
    const inputs = document.querySelectorAll("input[type='checkbox']");
    for(var i = 0; i < inputs.length; i++) {
        if(inputs[i].checked)
            data.push({id:inputs[i].name});   
    }
    fetch("/delete", {
        method: "POST", 
        body: JSON.stringify(data)
    }).then(result => {
        location.href = "index.html";
    });
}
async function addItemsToDropDown(dropDownId){
    const dropDown = document.getElementById(dropDownId);
    const data = await getDataJson('/data');{
        const newElement = document.createElement("OPTION");
        dropDown.appendChild(newElement);
    }
    for(x of data){
        if(x.status === 'open'){
            const newElement = document.createElement("OPTION");
            newElement.innerText = x.message;
            newElement.value = x.id;
            dropDown.appendChild(newElement);
        }
    }
}
document.addEventListener('DOMContentLoaded',() => {
    includeHTML('partials/navigation');
    includeHTML('partials/contact');
})