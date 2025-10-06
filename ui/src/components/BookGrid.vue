<template>
  <div class="book-grid">
    <BookCard v-for="book in books" :key="book.id" :book="book" />
  </div>
</template>

<script>
  import BookCard from "./BookCard.vue";
  export default {
    name: "BookGrid",
    props: { books: Array },
    components: { BookCard },
  };
</script>