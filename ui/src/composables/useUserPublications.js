import { ref } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { publicationsAPI } from '../api/publicationsService';

/**
 * Composable para obtener publicaciones filtradas por usuario
 */
export function useUserPublications() {
  const authStore = useAuthStore();
  const publications = ref([]);
  const loading = ref(false);
  const error = ref(null);

  /**
   * Carga todas las publicaciones del usuario actual
   */
  const loadMyPublications = async (page = 0, size = 20) => {
    loading.value = true;
    error.value = null;

    try {
      const userProfileId = authStore.currentUserProfileId;

      if (!userProfileId) {
        throw new Error('No hay usuario autenticado');
      }

      // Usar el endpoint específico para obtener publicaciones por usuario
      const response = await publicationsAPI.getByUser(userProfileId, page, size);
      publications.value = response.data?.data || [];

      return publications.value;
    } catch (err) {
      console.error('Error loading user publications:', err);
      error.value = err.response?.data?.message || 'Error al cargar publicaciones';
      return [];
    } finally {
      loading.value = false;
    }
  };

  /**
   * Verifica si una publicación pertenece al usuario actual
   */
  const isMyPublication = (publication) => {
    return publication.userProfileId === authStore.currentUserProfileId;
  };

  /**
   * Verifica si el usuario puede editar una publicación
   * (es el dueño o es admin)
   */
  const canEditPublication = (publication) => {
    return authStore.isAdmin || isMyPublication(publication);
  };

  /**
   * Verifica si el usuario puede eliminar una publicación
   * (es el dueño o es admin)
   */
  const canDeletePublication = (publication) => {
    return authStore.isAdmin || isMyPublication(publication);
  };

  return {
    publications,
    loading,
    error,
    loadMyPublications,
    isMyPublication,
    canEditPublication,
    canDeletePublication
  };
}