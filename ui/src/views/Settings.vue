<template>
  <div class="settings-container">
    <div class="settings-header">
      <h2 class="settings-title">Configuración</h2>
      <p class="settings-subtitle">Administra tu cuenta y preferencias</p>
    </div>

    <div class="settings-content">
      <div class="settings-sidebar">
        <button
          v-for="section in sections"
          :key="section.id"
          @click="activeSection = section.id"
          :class="['section-btn', { active: activeSection === section.id }]"
        >
          <component :is="section.icon" />
          {{ section.name }}
        </button>
      </div>

      <div class="settings-main">
        <!-- Perfil -->
        <div v-show="activeSection === 'profile'" class="settings-section">
          <h3 class="section-title">Perfil de Usuario</h3>
          <div class="form-group">
            <label>Foto de Perfil</label>
            <div class="profile-picture-upload">
              <div class="avatar-preview">
                {{ userInitial }}
              </div>
              <button class="btn-secondary">Cambiar Foto</button>
            </div>
          </div>

          <div class="form-group">
            <label for="displayName">Nombre para Mostrar</label>
            <input
              id="displayName"
              v-model="userProfile.displayName"
              type="text"
              class="form-input"
              placeholder="Tu nombre"
            />
          </div>

          <div class="form-group">
            <label for="username">Nombre de Usuario</label>
            <input
              id="username"
              v-model="userProfile.username"
              type="text"
              class="form-input"
              placeholder="@usuario"
            />
            <small class="form-help">El nombre de usuario no se puede cambiar</small>
          </div>

          <div class="form-group">
            <label for="email">Correo Electrónico</label>
            <input
              id="email"
              v-model="userProfile.email"
              type="email"
              class="form-input"
              placeholder="tu@email.com"
            />
            <small class="form-help">El correo electrónico no se puede cambiar</small>
          </div>

          <div class="form-group">
            <label for="bio">Biografía</label>
            <textarea
              id="bio"
              v-model="userProfile.bio"
              class="form-textarea"
              placeholder="Cuéntanos sobre ti..."
              rows="4"
            ></textarea>
          </div>

            <div class="form-group">
              <label for="password">Contraseña (Puedes usar nueva contraseña)</label>
              <input
                id="password"
                v-model="userProfile.password"
                type="password"
                class="form-input"
                placeholder="Contraseña"
              />
            </div>

           <div class="form-group">
             <label for="confirmPassword">Confirmar Contraseña</label>
             <input
               id="confirmPassword"
               v-model="userProfile.confirmPassword"
               type="password"
               class="form-input"
               placeholder="Confirma la contraseña"
             />
           </div>

           <button @click="saveProfile" class="btn-primary" :disabled="loading">
             {{ loading ? 'Guardando...' : 'Guardar Cambios' }}
           </button>
        </div>

        <!-- Privacidad -->
        <div v-show="activeSection === 'privacy'" class="settings-section">
          <h3 class="section-title">Privacidad</h3>

          <div class="danger-zone">
            <h4 class="danger-title">Zona de Peligro</h4>
            <p>Una vez que elimines tu cuenta, no hay vuelta atrás. Por favor, ten cuidado.</p>
            <button @click="deleteAccount" class="btn-danger">Eliminar Cuenta</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, h, onMounted, computed } from 'vue';
import { userProfilesAPI, usersAPI } from '../api/usersService';
import { useAuthStore } from '../stores/authStore';

// Iconos simples como componentes funcionales
const UserIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
  h('path', { d: 'M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2' }),
  h('circle', { cx: 12, cy: 7, r: 4 })
]);

const LockIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
  h('rect', { width: 18, height: 11, x: 3, y: 11, rx: 2, ry: 2 }),
  h('path', { d: 'M7 11V7a5 5 0 0 1 10 0v4' })
]);

const ShieldIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
  h('path', { d: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10' })
]);

export default {
  name: 'Settings',
  setup() {
    const authStore = useAuthStore();
    const activeSection = ref('profile');
    const loading = ref(false);
    const currentUserProfile = ref(null);

    const sections = [
       { id: 'profile', name: 'Perfil', icon: UserIcon },
       { id: 'privacy', name: 'Privacidad', icon: ShieldIcon }
    ];

     const userProfile = ref({
        displayName: '',
        username: '',
        email: '',
        bio: '',
        password: '',
        confirmPassword: ''
     });

    // Computed property para obtener la inicial del displayName
    const userInitial = computed(() => {
      return userProfile.value.displayName?.[0]?.toUpperCase() || 'U';
    });

    // Cargar datos del UserProfile del usuario autenticado
    const loadUserProfile = async () => {
      loading.value = true;
      try {
        const userProfileId = authStore.currentUserProfileId;
        if (!userProfileId) {
          console.error('No hay usuario autenticado');
          return;
        }

        const response = await userProfilesAPI.getProfileById(userProfileId);
        currentUserProfile.value = response.data;

        // Actualizar los campos del formulario con los datos reales
        userProfile.value = {
          displayName: response.data.displayName || '',
          username: response.data.user?.username || authStore.currentUsername || '',
          email: response.data.user?.email || authStore.userEmail || '',
          bio: response.data.bio || ''
        };
      } catch (error) {
        console.error('Error loading user profile:', error);
        alert('Error al cargar el perfil de usuario');
      } finally {
        loading.value = false;
      }
    };

    const saveProfile = async () => {
      if (!currentUserProfile.value) {
        alert('No se ha cargado el perfil de usuario');
        return;
      }

      if (!userProfile.value.displayName.trim()) {
        alert('El nombre para mostrar no puede estar vacío');
        return;
      }

      loading.value = true;
      try {
        const userProfileId = authStore.currentUserProfileId;
        const userId = currentUserProfile.value.user.id;

        // Actualizar datos del perfil (displayName, bio, foto)
        const updateProfileData = {
          userId: userId,
          displayName: userProfile.value.displayName,
          bio: userProfile.value.bio || '',
          profilePicture: currentUserProfile.value.profilePicture || ''
        };
        await userProfilesAPI.updateProfile(userProfileId, updateProfileData);

         // Validar nueva contraseña si se proporciona
         if (userProfile.value.password || userProfile.value.confirmPassword) {
           if (!userProfile.value.password || !userProfile.value.confirmPassword) {
             alert('Debes completar ambos campos de nueva contraseña');
             loading.value = false;
             return;
           }
           if (userProfile.value.password !== userProfile.value.confirmPassword) {
             alert('Las nuevas contraseñas no coinciden');
             loading.value = false;
             return;
           }
         }
         const updateUserData = {
           username: userProfile.value.username,
           email: userProfile.value.email
         };
         // Si se proporciona nueva contraseña, incluirla en el payload
         if (userProfile.value.password) {
           updateUserData.password = userProfile.value.password;
         }
         await usersAPI.updateAuthUser(userId, updateUserData);

         // Recargar el perfil para obtener los datos actualizados
         await loadUserProfile();
         userProfile.value.password = '';
         userProfile.value.confirmPassword = '';
         alert('Perfil actualizado correctamente');
      } catch (error) {
        console.error('Error updating profile:', error);
        alert('Error al actualizar el perfil: ' + (error.response?.data?.message || error.message));
      } finally {
        loading.value = false;
      }
    };

    const deleteAccount = () => {
      if (confirm('¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.')) {
        console.log('Eliminar cuenta');
        alert('Cuenta eliminada');
      }
    };

    // Cargar datos al montar el componente
    onMounted(() => {
      loadUserProfile();
    });

    return {
      activeSection,
      sections,
      userProfile,
      userInitial,
      loading,
      saveProfile,
      deleteAccount
    };
  }
};
</script>

<style scoped>
.settings-container {
  width: 85%;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.settings-header {
  margin-bottom: 32px;
}

.settings-title {
  font-size: 32px;
  font-weight: bold;
  color: #1a202c;
  margin-bottom: 8px;
}

.settings-subtitle {
  color: #718096;
  font-size: 16px;
  margin: 0;
}

.settings-content {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 32px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  min-height: 600px;
}

.settings-sidebar {
  background: #f7fafc;
  padding: 24px 0;
  border-right: 1px solid #e2e8f0;
  min-width: 250px;
}

.section-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: transparent;
  border: none;
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.section-btn:hover {
  background: white;
  color: #1a202c;
}

.section-btn.active {
  background: white;
  color: #667eea;
  border-left-color: #667eea;
}

.settings-main {
  padding: 32px 48px;
  min-width: 0;
  flex: 1;
  display: flex;
  justify-content: center;
}

.settings-section {
  width: 100%;
  max-width: 700px;
  min-width: 500px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 24px 0;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 8px;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1a202c;
  transition: all 0.2s;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input:disabled {
  background: #f7fafc;
  cursor: not-allowed;
}

.form-help {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #718096;
}

.profile-picture-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
}

.btn-primary,
.btn-secondary,
.btn-danger {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5568d3;
}

.btn-primary:disabled {
  background: #a0aec0;
  cursor: not-allowed;
  opacity: 0.6;
}

.btn-secondary {
  background: white;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f7fafc;
}

.btn-danger {
  background: #f56565;
  color: white;
}

.btn-danger:hover {
  background: #e53e3e;
}

.danger-zone {
  padding: 24px;
  background: #fff5f5;
  border: 1px solid #feb2b2;
  border-radius: 8px;
  margin-top: 32px;
}

.danger-title {
  font-size: 16px;
  font-weight: 600;
  color: #c53030;
  margin: 0 0 8px 0;
}

.danger-zone p {
  font-size: 14px;
  color: #742a2a;
  margin: 0 0 16px 0;
}

@media (max-width: 768px) {
  .settings-content {
    grid-template-columns: 1fr;
  }

  .settings-sidebar {
    border-right: none;
    border-bottom: 1px solid #e2e8f0;
    min-width: auto;
  }

  .settings-main {
    padding: 24px;
  }

  .settings-section {
    min-width: auto;
  }
}
</style>