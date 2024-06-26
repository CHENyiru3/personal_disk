<template>
<p> {{ a }}</p>
</template>


<script setup name="ok">
let a =ref(666);
a.value = 999;
</script>

