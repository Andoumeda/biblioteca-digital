<template>
  <div class="create-publication">
    <h2>Crear Nueva Publicación</h2>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="title">Título *</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
          required
          placeholder="Título de la publicación"
        />
      </div>

      <div class="form-group">
        <label for="description">Descripción</label>
        <textarea
          id="description"
          v-model="form.description"
          rows="4"
          placeholder="Descripción de la publicación"
        ></textarea>
      </div>

      <div class="form-group">
        <label for="userProfileId">ID del Usuario *</label>
        <input
          id="userProfileId"
          v-model.number="form.userProfileId"
          type="number"
          required
          placeholder="ID del perfil de usuario"
        />
      </div>

      <div class="form-group">
        <label for="state">Estado</label>
        <select id="state" v-model="form.state">
          <option value="PENDING">Pendiente</option>
          <option value="APPROVED">Aprobado</option>
          <option value="REJECTED">Rechazado</option>
        </select>
      </div>

      <div class="form-group">
        <label>Categorías</label>
        <div class="categories-checkboxes">
          <div v-for="category in categories" :key="category.id" class="checkbox-item">
            <input
              type="checkbox"
              :id="`cat-${category.id}`"
              :value="category.id"
              v-model="form.categoryIds"
            />
            <label :for="`cat-${category.id}`">{{ category.name }}</label>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? 'Creando...' : 'Crear Publicación' }}
        </button>
        <button type="button" @click="resetForm" class="btn-secondary">
          Cancelar
        </button>
      </div>

      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="success" class="success-message">¡Publicación creada exitosamente!</div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { usePublicationsStore } from '../stores/publicationsStore';
import { useCategories } from '../composables/useCategories';

const store = usePublicationsStore();
const { categories } = useCategories();

const loading = ref(false);
const error = ref(null);
const success = ref(false);

const form = reactive({
  title: '',
  description: '',
  userProfileId: null,
  state: 'PENDING',
  categoryIds: []
});

const handleSubmit = async () => {
  loading.value = true;
  error.value = null;
  success.value = false;

  try {
    // Preparar datos para enviar
    const publicationData = {
      title: form.title,
      description: form.description,
      userProfileId: form.userProfileId,
      state: form.state,
      categoryIds: form.categoryIds
    };

    await store.createPublication(publicationData);
    success.value = true;
    resetForm();

    // Ocultar mensaje de éxito después de 3 segundos
    setTimeout(() => {
      success.value = false;
    }, 3000);
  } catch (err) {
    error.value = err.message || 'Error al crear la publicación';
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.title = '';
  form.description = '';
  form.userProfileId = null;
  form.state = 'PENDING';
  form.categoryIds = [];
};
</script>

<style scoped>
.create-publication {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

input[type="text"],
input[type="number"],
textarea,
select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input:focus,
textarea:focus,
select:focus {
  outline: none;
  border-color: #007bff;
}

.categories-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.5rem 0;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.checkbox-item input[type="checkbox"] {
  width: auto;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-primary,
.btn-secondary {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-primary:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

.error-message {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #fee;
  color: #c33;
  border-radius: 4px;
}

.success-message {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #efe;
  color: #3c3;
  border-radius: 4px;
}
</style>