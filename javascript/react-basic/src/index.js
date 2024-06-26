

import React from 'react';
import ReactDOM from 'react-dom/client';

//导入项目的根组件
import App from './App';
//渲染到root，index.html中的root
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <App />
);

