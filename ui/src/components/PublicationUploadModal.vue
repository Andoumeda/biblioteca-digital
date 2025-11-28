<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <h2 class="modal-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
            </svg>
            {{ editMode ? 'Editar Publicación' : 'Subir Nueva Publicación de Libros' }}
          </h2>
          <p class="modal-description">
            {{ editMode ? 'Edita los detalles de tu publicación y sus libros.' : 'Crea una publicación con uno o más libros.' }} Todos los campos marcados con * son obligatorios.
          </p>
        </div>
        <button @click="$emit('close')" class="close-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <div v-if="uploadComplete" class="success-message">
        <div class="success-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <h3>¡Publicación Subida Exitosamente!</h3>
        <p>
          Tu publicación "{{ publicationData.title }}" con {{ books.length }} libro{{ books.length > 1 ? 's' : '' }}
          ha sido enviada para revisión. Te notificaremos cuando esté disponible en la plataforma.
        </p>
        <div class="success-actions">
          <button @click="resetForm" class="btn-outline">Subir otra Publicación</button>
          <button @click="$emit('close')" class="btn-primary">Volver a Mi Biblioteca</button>
        </div>
      </div>

      <form v-else @submit.prevent="handleSubmit" class="modal-body">
        <!-- Información de la Publicación -->
        <div class="section-card">
          <div class="section-header">
            <h3 class="section-title">Información de la Publicación</h3>
            <p class="section-description">Detalles generales que aplican a toda la publicación</p>
          </div>
          <div class="section-content">
            <div class="form-group">
              <label for="publication-title">Título de la Publicación *</label>
              <input
                id="publication-title"
                v-model="publicationData.title"
                type="text"
                placeholder="Ej: Guías de Programación Completas"
                required
              />
            </div>

            <!-- Categorías Seleccionadas -->
            <div class="form-group">
              <label>Categorías * (mínimo 1)</label>
              <div class="selected-categories">
                <div v-if="publicationData.selectedCategories.length === 0" class="no-categories">
                  No hay categorías seleccionadas. Selecciona al menos una categoría de la lista.
                </div>
                <div v-else class="category-chips">
                  <div v-for="category in publicationData.selectedCategories" :key="category.id" class="category-chip">
                    <span>{{ category.name }}</span>
                    <button type="button" @click="removeCategoryFromPublication(category.id)" class="chip-remove">
                      <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 6L6 18M6 6l12 12"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Selector de Categorías -->
            <div class="form-group">
              <label for="publication-category">Agregar Categoría</label>
              <select
                id="publication-category"
                v-model="selectedCategoryToAdd"
                @change="addCategoryToPublication"
                :disabled="loadingCategories"
              >
                <option value="">{{ loadingCategories ? 'Cargando categorías...' : 'Selecciona una categoría para agregar' }}</option>
                <option
                  v-for="category in availableCategories"
                  :key="category.id"
                  :value="category.id"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="publication-description">Descripción de la Publicación</label>
              <textarea
                id="publication-description"
                v-model="publicationData.description"
                rows="3"
                placeholder="Describe brevemente tu publicación de libros..."
              ></textarea>
            </div>
          </div>
        </div>

        <!-- Libros -->
        <div class="section-card">
          <div class="section-header">
            <h3 class="section-title">
              Libros de la Publicación ({{ books.length }})
            </h3>
            <button type="button" @click="addBook" class="btn-add">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 5v14M5 12h14"/>
              </svg>
              Agregar Libro
            </button>
          </div>

          <div v-for="(book, index) in books" :key="book.id" class="book-form">
            <div class="book-header">
              <h4>Libro {{ index + 1 }}</h4>
              <button
                v-if="books.length > 1"
                type="button"
                @click="removeBook(book.id)"
                class="btn-remove"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                </svg>
                Eliminar
              </button>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label :for="'book-title-' + book.id">Título del Libro *</label>
                <input
                  :id="'book-title-' + book.id"
                  v-model="book.title"
                  type="text"
                  placeholder="Título del libro"
                  required
                />
              </div>
            </div>

            <!-- Autores Seleccionados -->
            <div class="form-group">
              <label>Autores * (mínimo 1)</label>
              <div class="selected-authors">
                <div v-if="book.selectedAuthors.length === 0" class="no-authors">
                  No hay autores seleccionados. Busca un autor existente o crea uno nuevo.
                </div>
                <div v-else class="author-chips">
                  <div v-for="author in book.selectedAuthors" :key="author.id" class="author-chip">
                    <span>{{ author.fullName || 'Autor sin nombre' }}</span>
                    <button type="button" @click="removeAuthorFromBook(book.id, author.id)" class="chip-remove">
                      <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 6L6 18M6 6l12 12"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Buscador de Autores -->
            <div class="form-group">
              <label :for="'author-search-' + book.id">Buscar Autor Existente</label>
              <div class="search-container">
                <input
                  :id="'author-search-' + book.id"
                  v-model="authorSearchQuery[book.id]"
                  @input="searchAuthors(book.id, authorSearchQuery[book.id])"
                  type="text"
                  placeholder="Escribe el nombre del autor..."
                  class="search-input"
                />
                <button type="button" @click="toggleAuthorForm(book.id)" class="btn-create-author">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 5v14M5 12h14"/>
                  </svg>
                  Crear Nuevo Autor
                </button>
              </div>

              <!-- Lista de Autores -->
              <div v-if="authorSearchQuery[book.id] !== undefined && authorSearchQuery[book.id] !== ''" class="authors-dropdown">
                <div v-if="loadingAuthors" class="loading-authors">Buscando autores...</div>
                <div v-else-if="availableAuthors.length === 0" class="no-results">
                  No se encontraron autores. Intenta con otro nombre o crea uno nuevo.
                </div>
                <div v-else class="author-list">
                  <button
                    v-for="author in availableAuthors"
                    :key="author.id"
                    type="button"
                    @click="addAuthorToBook(book.id, author)"
                    class="author-item"
                    :disabled="book.selectedAuthors.some(a => a.id === author.id)"
                  >
                    <div class="author-info">
                      <div class="author-name">{{ author.fullName || 'Autor sin nombre' }}</div>
                      <div class="author-details">ID: {{ author.id }}</div>
                    </div>
                    <span v-if="book.selectedAuthors.some(a => a.id === author.id)" class="already-selected">✓</span>
                  </button>
                </div>
              </div>
            </div>

            <!-- Formulario para Crear Nuevo Autor -->
            <div v-if="showAuthorForm[book.id]" class="new-author-form">
              <div class="form-header">
                <h5>Crear Nuevo Autor</h5>
                <button type="button" @click="toggleAuthorForm(book.id)" class="close-form-btn">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M18 6L6 18M6 6l12 12"/>
                  </svg>
                </button>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Nombre Completo *</label>
                  <input
                    v-model="newAuthorData[book.id].fullName"
                    type="text"
                    placeholder="Ej: Gabriel García Márquez"
                    required
                  />
                </div>
                <div class="form-group">
                  <label>Nacionalidad</label>
                  <input
                    v-model="newAuthorData[book.id].nationality"
                    type="text"
                    placeholder="Ej: Colombia"
                  />
                </div>
              </div>

              <div class="form-group">
                <label>Fecha de Nacimiento</label>
                <input
                  v-model="newAuthorData[book.id].birthDate"
                  type="date"
                />
              </div>

              <div class="form-group">
                <label>Biografía</label>
                <textarea
                  v-model="newAuthorData[book.id].bio"
                  rows="3"
                  placeholder="Breve biografía del autor..."
                ></textarea>
              </div>

              <button type="button" @click="createNewAuthor(book.id)" class="btn-submit-author">
                Crear y Agregar Autor
              </button>
            </div>

            <div class="form-group">
              <label :for="'book-description-' + book.id">Descripción del Libro</label>
              <textarea
                :id="'book-description-' + book.id"
                v-model="book.description"
                rows="3"
                placeholder="Breve descripción del contenido del libro..."
              ></textarea>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label :for="'book-url-' + book.id">URL del Libro (PDF/Archivo) *</label>
                <input
                  :id="'book-url-' + book.id"
                  v-model="book.bookUrl"
                  type="url"
                  placeholder="https://ejemplo.com/libro.pdf"
                  required
                />
              </div>

              <div class="form-group">
                <label :for="'book-cover-' + book.id">URL de la Portada</label>
                <input
                  :id="'book-cover-' + book.id"
                  v-model="book.coverImg"
                  type="url"
                  placeholder="https://ejemplo.com/portada.jpg"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Progress Bar -->
        <div v-if="isUploading" class="upload-progress">
          <div class="progress-info">
            <span>Subiendo publicación...</span>
            <span>{{ uploadProgress }}%</span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
          </div>
        </div>

        <!-- Acciones -->
        <div class="modal-footer">
          <button type="button" @click="$emit('close')" class="btn-outline" :disabled="isUploading">
            Cancelar
          </button>
          <button type="submit" class="btn-primary" :disabled="!isFormValid() || isUploading">
            <svg v-if="!isUploading" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="17 8 12 3 7 8"/>
              <line x1="12" x2="12" y1="3" y2="15"/>
            </svg>
            {{ isUploading ? (editMode ? 'Actualizando...' : 'Subiendo...') : (editMode ? 'Actualizar Publicación' : 'Subir Publicación') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed } from 'vue';
import { categoriesAPI, publicationsAPI } from '../api/publicationsService';
import { booksAPI, authorsAPI } from '../api/booksService';
import { useAuthStore } from '../stores/authStore';

export default {
  name: 'PublicationUploadModal',
  props: {
    editMode: {
      type: Boolean,
      default: false
    },
    publicationData: {
      type: Object,
      default: null
    }
  },
  emits: ['close', 'success'],
  setup(props, { emit }) {
    const authStore = useAuthStore();
    const currentUserProfileId = computed(() => authStore.currentUserProfileId);

    const publicationData = ref({
      id: null,
      title: '',
      description: '',
      selectedCategories: [],
      userProfileId: currentUserProfileId.value
    });

    const selectedCategoryToAdd = ref('');

    const books = ref([
      {
        id: '1',
        title: '',
        selectedAuthors: [],
        description: '',
        bookUrl: '',
        coverImg: ''
      }
    ]);

    const categories = ref([]);
    const loadingCategories = ref(true);

    // Estados para autores
    const availableAuthors = ref([]);
    const loadingAuthors = ref(false);
    const authorSearchQuery = ref({});
    const showAuthorForm = ref({});
    const newAuthorData = ref({});

    const uploadProgress = ref(0);
    const isUploading = ref(false);
    const uploadComplete = ref(false);

    // Computed property para categorías disponibles (excluye las ya seleccionadas)
    const availableCategories = computed(() => {
      return categories.value.filter(category =>
        !publicationData.value.selectedCategories.some(selected => selected.id === category.id)
      );
    });

    // Agregar categoría a la publicación
    const addCategoryToPublication = () => {
      if (selectedCategoryToAdd.value) {
        const category = categories.value.find(cat => cat.id === selectedCategoryToAdd.value);
        if (category && !publicationData.value.selectedCategories.some(c => c.id === category.id)) {
          publicationData.value.selectedCategories.push(category);
        }
        selectedCategoryToAdd.value = '';
      }
    };

    // Eliminar categoría de la publicación
    const removeCategoryFromPublication = (categoryId) => {
      publicationData.value.selectedCategories = publicationData.value.selectedCategories.filter(
        cat => cat.id !== categoryId
      );
    };

    // Cargar categorías desde la API
    const loadCategories = async () => {
      try {
        loadingCategories.value = true;
        const response = await categoriesAPI.getAll(0, 100); // Obtener hasta 100 categorías
        if (response.data && response.data.data) {
          categories.value = response.data.data;
        }
      } catch (error) {
        console.error('Error al cargar categorías:', error);
      } finally {
        loadingCategories.value = false;
      }
    };

    // Cargar datos en modo de edición
    const loadEditData = () => {
      if (props.editMode && props.publicationData) {
        publicationData.value = {
          id: props.publicationData.id,
          title: props.publicationData.title || '',
          description: props.publicationData.description || '',
          selectedCategories: props.publicationData.categories || [],
          userProfileId: props.publicationData.userProfileId || currentUserProfileId.value
        };

        // Cargar libros si están disponibles
        if (props.publicationData.books && props.publicationData.books.length > 0) {
          books.value = props.publicationData.books.map(book => ({
            id: book.id.toString(),
            title: book.title || '',
            selectedAuthors: book.authors || [],
            description: book.description || '',
            bookUrl: book.bookUrl || '',
            coverImg: book.coverImg || ''
          }));
        }
      }
    };

    // Cargar categorías al montar el componente
    onMounted(() => {
      loadCategories();
      loadAuthors(); // Cargar autores iniciales
      loadEditData(); // Cargar datos si está en modo edición
    });

    // Observar cambios en los props
    watch(() => props.publicationData, () => {
      if (props.editMode && props.publicationData) {
        loadEditData();
      }
    }, { immediate: true });

    // Cargar autores desde la API
    const loadAuthors = async (fullname = '-') => {
      try {
        loadingAuthors.value = true;
        const response = await authorsAPI.getAuthorsByFilters({ fullname }, 0);
        if (response.data && response.data.data) {
          availableAuthors.value = response.data.data.slice(0, 10); // Máximo 10 autores
        }
      } catch (error) {
        console.error('Error al cargar autores:', error);
      } finally {
        loadingAuthors.value = false;
      }
    };

    // Buscar autores por nombre
    const searchAuthors = async (bookId, query) => {
      authorSearchQuery.value[bookId] = query;
      if (query && query.trim() !== '') {
        await loadAuthors(query.trim());
      } else {
        await loadAuthors(); // Cargar lista inicial si está vacío
      }
    };

    // Agregar autor a un libro
    const addAuthorToBook = (bookId, author) => {
      const book = books.value.find(b => b.id === bookId);
      if (book && !book.selectedAuthors.some(a => a.id === author.id)) {
        book.selectedAuthors.push(author);
      }
      // Limpiar búsqueda
      authorSearchQuery.value[bookId] = '';
    };

    // Remover autor de un libro
    const removeAuthorFromBook = (bookId, authorId) => {
      const book = books.value.find(b => b.id === bookId);
      if (book) {
        book.selectedAuthors = book.selectedAuthors.filter(a => a.id !== authorId);
      }
    };

    // Mostrar/ocultar formulario de nuevo autor
    const toggleAuthorForm = (bookId) => {
      showAuthorForm.value[bookId] = !showAuthorForm.value[bookId];
      if (showAuthorForm.value[bookId]) {
        newAuthorData.value[bookId] = {
          fullName: '',
          birthDate: '',
          nationality: '',
          bio: ''
        };
      }
    };

    // Crear nuevo autor
    const createNewAuthor = async (bookId) => {
      const authorData = newAuthorData.value[bookId];
      if (!authorData.fullName || !authorData.fullName.trim()) {
        alert('El nombre del autor es obligatorio');
        return;
      }

      try {
        const payload = {
          fullName: authorData.fullName,
          bio: authorData.bio || '', // Bio is required by API, send empty string if not provided
          birthDate: authorData.birthDate || null,
          nationality: authorData.nationality || null
        };

        const response = await authorsAPI.create(payload);
        if (response.data && response.data.data) {
          const newAuthor = response.data.data;
          // Agregar el nuevo autor al libro
          addAuthorToBook(bookId, newAuthor);
          // Recargar lista de autores
          await loadAuthors();
          // Cerrar formulario
          showAuthorForm.value[bookId] = false;
          alert('Autor creado exitosamente');
        }
      } catch (error) {
        console.error('Error al crear autor:', error);
        alert('Error al crear el autor. Por favor, intenta de nuevo.');
      }
    };

    const addBook = () => {
      const newBook = {
        id: Date.now().toString(),
        title: '',
        selectedAuthors: [],
        description: '',
        bookUrl: '',
        coverImg: ''
      };
      books.value.push(newBook);
    };

    const removeBook = (bookId) => {
      if (books.value.length > 1) {
        books.value = books.value.filter(book => book.id !== bookId);
      }
    };

    const isFormValid = () => {
      const publicationValid = publicationData.value.title.trim() !== '' &&
                               publicationData.value.selectedCategories.length > 0;
      const booksValid = books.value.every(
        book => book.title.trim() !== '' &&
                book.selectedAuthors.length > 0 &&
                book.bookUrl.trim() !== ''
      );
      return publicationValid && booksValid && books.value.length > 0;
    };

    const handleSubmit = async () => {
      if (!isFormValid()) return;

      isUploading.value = true;
      uploadProgress.value = 0;

      try {
        let publicationId;

        if (props.editMode && publicationData.value.id) {
          // MODO EDICIÓN: Actualizar publicación existente
          uploadProgress.value = 10;
          const publicationPayload = {
            title: publicationData.value.title,
            description: publicationData.value.description,
            userProfileId: publicationData.value.userProfileId,
            categoryIds: publicationData.value.selectedCategories.map(cat => cat.id)
          };

          console.log('Actualizando publicación:', publicationData.value.id, publicationPayload);
          await publicationsAPI.update(publicationData.value.id, publicationPayload);
          publicationId = publicationData.value.id;

          uploadProgress.value = 30;

          // Eliminar libros antiguos que no están en la lista actual
          const currentBookIds = books.value
            .filter(book => !isNaN(parseInt(book.id)))
            .map(book => parseInt(book.id));

          const originalBooks = props.publicationData?.books || [];
          for (const oldBook of originalBooks) {
            if (!currentBookIds.includes(oldBook.id)) {
              console.log('Eliminando libro:', oldBook.id);
              await booksAPI.deleteBook(oldBook.id);
            }
          }

          uploadProgress.value = 40;

          // Actualizar o crear libros
          const booksCount = books.value.length;
          const progressPerBook = 60 / booksCount;

          for (let i = 0; i < books.value.length; i++) {
            const book = books.value[i];

            // Payload del libro sin authorIds (el backend no lo procesa)
            const bookPayload = {
              title: book.title,
              description: book.description,
              bookUrl: book.bookUrl,
              coverImg: book.coverImg,
              publicationId: publicationId
            };

            let bookId;

            // Si el ID es numérico, es un libro existente que se debe actualizar
            if (!isNaN(parseInt(book.id))) {
              console.log('Actualizando libro:', book.id, bookPayload);
              await booksAPI.updateBook(parseInt(book.id), bookPayload);
              bookId = parseInt(book.id);
            } else {
              // Si no es numérico, es un libro nuevo
              console.log('Creando nuevo libro:', bookPayload);
              const bookResponse = await booksAPI.createBook(bookPayload);
              bookId = bookResponse.data.id;
            }

            // Manejar relaciones libro-autor
            // Nota: En modo edición, sería ideal eliminar las relaciones antiguas y crear las nuevas
            // Por simplicidad, aquí solo creamos las nuevas relaciones si es un libro nuevo
            // Para un libro existente, las relaciones deberían manejarse en un flujo más complejo
            if (isNaN(parseInt(book.id))) {
              // Solo crear relaciones para libros nuevos
              for (const author of book.selectedAuthors) {
                const bookAuthorPayload = {
                  bookId: bookId,
                  authorId: author.id,
                  contributionType: 'PRINCIPAL'
                };

                try {
                  await booksAPI.createBookAuthor(bookAuthorPayload);
                  console.log(`Relación libro-autor creada: libro ${bookId}, autor ${author.id}`);
                } catch (error) {
                  console.error(`Error al crear relación libro-autor:`, error);
                }
              }
            }

            uploadProgress.value = 40 + ((i + 1) * progressPerBook);
          }
        } else {
          // MODO CREACIÓN: Crear nueva publicación
          uploadProgress.value = 20;
          const publicationPayload = {
            title: publicationData.value.title,
            description: publicationData.value.description,
            userProfileId: publicationData.value.userProfileId,
            categoryIds: publicationData.value.selectedCategories.map(cat => cat.id)
          };

          console.log('Creando publicación:', publicationPayload);
          const publicationResponse = await publicationsAPI.createPublication(publicationPayload);

          if (!publicationResponse.data) {
            throw new Error('Error al crear la publicación');
          }

          publicationId = publicationResponse.data.id;
          console.log('Publicación creada con ID:', publicationId);

          uploadProgress.value = 40;

          // 2. Crear los libros asociados a la publicación
          const booksCount = books.value.length;
          const progressPerBook = 60 / booksCount;

          for (let i = 0; i < books.value.length; i++) {
            const book = books.value[i];

            // Crear el libro sin authorIds (el backend no lo procesa)
            const bookPayload = {
              title: book.title,
              description: book.description,
              bookUrl: book.bookUrl,
              coverImg: book.coverImg,
              publicationId: publicationId
            };

            console.log('Creando libro:', bookPayload);
            const bookResponse = await booksAPI.createBook(bookPayload);

            if (!bookResponse.data) {
              console.error('Error al crear libro:', book.title);
            } else {
              console.log('Libro creado con ID:', bookResponse.data.id);

              // Crear las relaciones libro-autor
              const createdBookId = bookResponse.data.id;
              for (const author of book.selectedAuthors) {
                const bookAuthorPayload = {
                  bookId: createdBookId,
                  authorId: author.id,
                  contributionType: 'PRINCIPAL' // Tipo de contribución por defecto
                };

                try {
                  await booksAPI.createBookAuthor(bookAuthorPayload);
                  console.log(`Relación libro-autor creada: libro ${createdBookId}, autor ${author.id}`);
                } catch (error) {
                  console.error(`Error al crear relación libro-autor:`, error);
                }
              }
            }

            uploadProgress.value = 40 + ((i + 1) * progressPerBook);
          }
        }

        uploadProgress.value = 100;
        isUploading.value = false;
        uploadComplete.value = true;
        emit('success');
      } catch (error) {
        console.error('Error al subir la publicación:', error);
        const errorMessage = error.response?.data?.message || error.message || 'Error desconocido';
        alert(`Error al subir la publicación: ${errorMessage}\nPor favor, intenta de nuevo.`);
        isUploading.value = false;
        uploadProgress.value = 0;
      }
    };

    const resetForm = () => {
      publicationData.value = {
        title: '',
        description: '',
        selectedCategories: [],
        userProfileId: currentUserProfileId.value
      };
      books.value = [
        {
          id: '1',
          title: '',
          selectedAuthors: [],
          description: '',
          bookUrl: '',
          coverImg: ''
        }
      ];
      selectedCategoryToAdd.value = '';
      uploadProgress.value = 0;
      isUploading.value = false;
      uploadComplete.value = false;
    };

    return {
      publicationData,
      books,
      categories,
      loadingCategories,
      availableCategories,
      selectedCategoryToAdd,
      availableAuthors,
      loadingAuthors,
      authorSearchQuery,
      showAuthorForm,
      newAuthorData,
      uploadProgress,
      isUploading,
      uploadComplete,
      addBook,
      removeBook,
      isFormValid,
      handleSubmit,
      resetForm,
      searchAuthors,
      addAuthorToBook,
      removeAuthorFromBook,
      toggleAuthorForm,
      createNewAuthor,
      addCategoryToPublication,
      removeCategoryFromPublication
    };
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  overflow-y: auto;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 900px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.modal-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-title svg {
  color: #3b82f6;
}

.modal-description {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  color: #6b7280;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: #f3f4f6;
  color: #111827;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.section-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.section-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 4px 0;
}

.section-description {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.section-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  color: #111827;
  background: white;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group textarea {
  resize: vertical;
  font-family: inherit;
}

.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add:hover {
  background: #2563eb;
}

.book-form {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.book-form:last-child {
  margin-bottom: 0;
}

.book-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.book-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.btn-remove {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #fee;
  color: #dc2626;
  border: 1px solid #fecaca;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove:hover {
  background: #fecaca;
}

.upload-progress {
  margin: 20px 0;
  padding: 16px;
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 8px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #1e40af;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #dbeafe;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #3b82f6;
  transition: width 0.3s ease;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-outline,
.btn-primary {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-outline {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-outline:hover:not(:disabled) {
  background: #f9fafb;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-outline:disabled,
.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.success-message {
  padding: 48px 24px;
  text-align: center;
}

.success-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: #dcfce7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.success-icon svg {
  color: #16a34a;
}

.success-message h3 {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px 0;
}

.success-message p {
  font-size: 16px;
  color: #6b7280;
  margin: 0 0 32px 0;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* Estilos para categorías */
.selected-categories {
  padding: 12px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  min-height: 60px;
}

.no-categories {
  color: #9ca3af;
  font-size: 14px;
  text-align: center;
  padding: 8px;
}

.category-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #d1fae5;
  color: #065f46;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

/* Estilos para autores */
.selected-authors {
  padding: 12px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  min-height: 60px;
}

.no-authors {
  color: #9ca3af;
  font-size: 14px;
  text-align: center;
  padding: 8px;
}

.author-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.author-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #dbeafe;
  color: #1e40af;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.chip-remove {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  display: flex;
  align-items: center;
  color: #1e40af;
  transition: color 0.2s;
}

.chip-remove:hover {
  color: #dc2626;
}

.search-container {
  display: flex;
  gap: 8px;
}

.search-input {
  flex: 1;
}

.btn-create-author {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-create-author:hover {
  background: #059669;
}

.authors-dropdown {
  margin-top: 8px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  max-height: 300px;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.loading-authors,
.no-results {
  padding: 16px;
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}

.author-list {
  display: flex;
  flex-direction: column;
}

.author-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border: none;
  background: white;
  cursor: pointer;
  transition: background 0.2s;
  text-align: left;
  border-bottom: 1px solid #f3f4f6;
}

.author-item:last-child {
  border-bottom: none;
}

.author-item:hover:not(:disabled) {
  background: #f9fafb;
}

.author-item:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 2px;
}

.author-details {
  font-size: 12px;
  color: #6b7280;
}

.already-selected {
  color: #10b981;
  font-weight: 700;
  font-size: 18px;
}

.new-author-form {
  margin-top: 16px;
  padding: 20px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-header h5 {
  font-size: 16px;
  font-weight: 600;
  color: #065f46;
  margin: 0;
}

.close-form-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #065f46;
  transition: color 0.2s;
}

.close-form-btn:hover {
  color: #dc2626;
}

.btn-submit-author {
  width: 100%;
  padding: 10px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 12px;
}

.btn-submit-author:hover {
  background: #059669;
}
</style>