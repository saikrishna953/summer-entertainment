(this["webpackJsonplpi-input"]=this["webpackJsonplpi-input"]||[]).push([[0],{15:function(e,t,n){e.exports=n(40)},20:function(e,t,n){},21:function(e,t,n){},39:function(e,t,n){},40:function(e,t,n){"use strict";n.r(t);var a=n(0),o=n.n(a),r=n(12),l=n.n(r),s=(n(20),n(21),n(13)),c=n(2),i=n(14),u=n.n(i),m=(n(39),function(e){var t={"Access-Control-Allow-Origin":"*","Content-type":"application/json"},n=Object(a.useState)({body:"Paste your chat here.."}),r=Object(c.a)(n,2),l=r[0],i=r[1],m=Object(a.useState)({responseData:"Waiting for input.."}),p=Object(c.a)(m,2),h=p[0],d=p[1],f=window.location.host;console.log("hostname-"+f);return o.a.createElement("div",null,o.a.createElement("form",{onSubmit:function(e){e.preventDefault(),console.log(l),d({responseData:"Waiting for response from server"}),u.a.post("http://"+f+"/summerEntertainment/sheets/update",l,{headers:t}).then((function(e){console.log(e),d({responseData:e.data})})).catch((function(e){console.log(e);var t=null;t=e.response?"Server side error, verify with programmer":e.request?"Network side error, contact admin":"I cannot do anything. Close the app.",d({responseData:t})}))},onReset:function(e){e.preventDefault(),console.log("in reset"),d({responseData:"Waiting for input.."}),i({body:"Paste your chat here.."})}},o.a.createElement("div",null,o.a.createElement("label",{style:{fontFamily:"sans-serif"}},"Chat Text:"),o.a.createElement("br",null),o.a.createElement("textarea",{name:"body",style:{border:"2px Light blue",height:"480px",width:"500px"},value:l.body,onChange:function(e){i(Object(s.a)({},e.target.name,e.target.value))},onFocus:function(e){e.preventDefault(),"Paste your chat here.."===l.body&&i({body:""})}}),o.a.createElement("br",null),o.a.createElement("div",null,o.a.createElement("button",{className:"buttonStyle",type:"submit"},"Submit"),o.a.createElement("button",{className:"buttonStyle",type:"reset"},"Reset"))),o.a.createElement("div",null,o.a.createElement("p",null,h.responseData))))});var p=function(){return o.a.createElement("div",{className:"App"},o.a.createElement(m,null))};Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));l.a.render(o.a.createElement(o.a.StrictMode,null,o.a.createElement(p,null)),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()})).catch((function(e){console.error(e.message)}))}},[[15,1,2]]]);
//# sourceMappingURL=main.f0bdaa45.chunk.js.map