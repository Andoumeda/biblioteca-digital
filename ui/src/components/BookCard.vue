<template>
  <div class="book-card">
    <div class="cover">
      <img :src="book.imagen" :alt="book.titulo" />
      <span class="tag">{{ book.categoria }}</span>
    </div>

    <h3>{{ book.titulo }}</h3>
    <p class="author">por {{ book.autor }}</p>
    <p class="desc">{{ book.descripcion }}</p>

    <div class="meta">
      <span>⭐ {{ book.rating }} ({{ book.votos }})</span>
      <span>❤️ {{ book.favoritos }}</span>
      <span>💬 {{ book.comentarios }}</span>
    </div>

    <div class="footer">
      <div class="user">
        <img class="avatar" src="https://placehold.co/30" />
        <span>{{ book.usuario }}</span>
      </div>
      <span class="date">{{ book.fecha }}</span>
    </div>

    <div class="actions">
      <button>🤍 Me gusta</button>
      <button>🔗 Compartir</button>
    </div>
  </div>
</template>

<script>
    export default {
    name: "BookCard",
    props: { book: Object },
    };
</script>