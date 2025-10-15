import { ref, onMounted } from 'vue';
import { categoriesAPI } from '../api/publicationsService';

export function useCategories() {
  const categories = ref([]);
  const loading = ref(false);
  const error = ref(null);

  const fetchCategories = async () => {
    loading.value = true;
    error.value = null;

    try {
      const response = await categoriesAPI.getAll();
      categories.value = response.data.data || [];
    } catch (err) {
      error.value = err.message || 'Error al cargar categorías';
      console.error('Error fetching categories:', err);
    } finally {
      loading.value = false;
    }
  };

  onMounted(() => {
    fetchCategories();
  });

  return {
    categories,
    loading,
    error,
    fetchCategories,
  };
}