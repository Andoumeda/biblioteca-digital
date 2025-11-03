<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-header">
        <div class="logo-container">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="48"
            height="48"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="logo-icon"
          >
            <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
          </svg>
        </div>
        <h1 class="app-title">DigiBooks Source</h1>
        <p class="app-subtitle">{{ isLogin ? 'Inicia sesión en tu cuenta' : 'Crea tu cuenta nueva' }}</p>
      </div>

      <div class="login-card">
        <div class="card-header">
          <h2 class="card-title">{{ isLogin ? 'Iniciar Sesión' : 'Registrarse' }}</h2>
          <p class="card-description">
            {{ isLogin ? 'Ingresa tus credenciales para acceder' : 'Completa los datos para crear tu cuenta' }}
          </p>
        </div>

        <div class="card-content">
          <form @submit.prevent="handleSubmit" class="login-form">
            <div v-if="!isLogin" class="form-group">
              <label class="form-label">Nombre completo</label>
              <div class="input-wrapper">
                <svg
                  class="input-icon"
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  v-model="name"
                  type="text"
                  placeholder="Tu nombre completo"
                  class="form-input with-icon"
                />
              </div>
            </div>

            <div v-if="!isLogin" class="form-group">
              <label class="form-label">Nombre de usuario</label>
              <div class="input-wrapper">
                <svg
                  class="input-icon"
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  v-model="username"
                  type="text"
                  placeholder="tu_usuario"
                  class="form-input with-icon"
                  required
                />
              </div>
            </div>

            <div v-if="isLogin" class="form-group">
              <label class="form-label">Nombre de usuario</label>
              <div class="input-wrapper">
                <svg
                  class="input-icon"
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  v-model="username"
                  type="text"
                  placeholder="tu_usuario"
                  class="form-input with-icon"
                  required
                />
              </div>
            </div>

            <div v-if="!isLogin" class="form-group">
              <label class="form-label">Correo electrónico</label>
              <div class="input-wrapper">
                <svg
                  class="input-icon"
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <rect width="20" height="16" x="2" y="4" rx="2" />
                  <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
                </svg>
                <input
                  v-model="email"
                  type="email"
                  placeholder="tu@email.com"
                  class="form-input with-icon"
                  required
                />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Contraseña</label>
              <div class="input-wrapper">
                <svg
                  class="input-icon"
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
                <input
                  v-model="password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="Tu contraseña"
                  class="form-input with-icon with-toggle"
                  required
                />
                <button
                  type="button"
                  @click="togglePasswordVisibility"
                  class="toggle-password-btn"
                  :title="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
                >
                  <!-- Icono de ojo abierto -->
                  <svg
                    v-if="!showPassword"
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                  <!-- Icono de ojo cerrado/tachado -->
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                    <line x1="2" x2="22" y1="2" y2="22" />
                  </svg>
                </button>
              </div>
            </div>

            <div v-if="!isLogin" class="form-group">
              <label class="form-label">Confirmar contraseña</label>
              <div class="input-wrapper">
                <svg
                  class="input-icon"
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
                <input
                  v-model="confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="Confirma tu contraseña"
                  class="form-input with-icon with-toggle"
                  required
                />
                <button
                  type="button"
                  @click="toggleConfirmPasswordVisibility"
                  class="toggle-password-btn"
                  :title="showConfirmPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
                >
                  <!-- Icono de ojo abierto -->
                  <svg
                    v-if="!showConfirmPassword"
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                  <!-- Icono de ojo cerrado/tachado -->
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                    <line x1="2" x2="22" y1="2" y2="22" />
                  </svg>
                </button>
              </div>
            </div>

            <div v-if="isLogin" class="forgot-password">
              <button type="button" class="link-button">¿Olvidaste tu contraseña?</button>
            </div>

            <div v-if="errorMessage" class="error-message">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="error-icon"
              >
                <circle cx="12" cy="12" r="10" />
                <line x1="12" x2="12" y1="8" y2="12" />
                <line x1="12" x2="12.01" y1="16" y2="16" />
              </svg>
              {{ errorMessage }}
            </div>

            <button type="submit" class="submit-button" :disabled="loading">
              {{ loading ? 'Procesando...' : (isLogin ? 'Iniciar Sesión' : 'Crear Cuenta') }}
            </button>
          </form>

          <div class="divider">
            <span>o</span>
          </div>

          <div class="toggle-mode">
            <p>
              {{ isLogin ? '¿No tienes una cuenta?' : '¿Ya tienes una cuenta?' }}
              <button type="button" @click="toggleMode" class="link-button-primary">
                {{ isLogin ? 'Regístrate' : 'Inicia sesión' }}
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/authStore';

export default {
  name: 'Login',
  data() {
    return {
      isLogin: true,
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      errorMessage: '',
      loading: false,
      showPassword: false,
      showConfirmPassword: false
    }
  },
  methods: {
    async handleSubmit() {
      this.errorMessage = '';
      this.loading = true;

      try {
        // Validar que las contraseñas coincidan en modo registro
        if (!this.isLogin) {
          if (this.password !== this.confirmPassword) {
            this.errorMessage = 'Las contraseñas no coinciden';
            return;
          }
          if (this.password.length < 8) {
            this.errorMessage = 'La contraseña debe tener al menos 8 caracteres';
            return;
          }
          if (!this.username.trim()) {
            this.errorMessage = 'El nombre de usuario es requerido';
            return;
          }
          if (!this.email.trim()) {
            this.errorMessage = 'El email es requerido';
            return;
          }
        } else {
          // Validaciones para login
          if (!this.username.trim()) {
            this.errorMessage = 'El nombre de usuario es requerido';
            return;
          }
        }

        const authStore = useAuthStore();

        if (this.isLogin) {
          // Login
          const result = await authStore.login(this.username, this.password);

          if (result.success) {
            console.log('Login exitoso:', result.user);
            this.$router.push('/');
          } else {
            this.errorMessage = result.error || 'Error al iniciar sesión';
          }
        } else {
          // Registro
          const result = await authStore.register({
            username: this.username,
            email: this.email,
            password: this.password
          });

          if (result.success) {
            console.log('Registro exitoso:', result.user);
            this.$router.push('/');
          } else {
            this.errorMessage = result.error || 'Error al registrar usuario';
          }
        }
      } catch (error) {
        console.error('Error en submit:', error);
        this.errorMessage = 'Error de conexión. Inténtalo de nuevo.';
      } finally {
        this.loading = false;
      }
    },

    toggleMode() {
      this.isLogin = !this.isLogin;
      this.username = '';
      this.email = '';
      this.password = '';
      this.confirmPassword = '';
      this.errorMessage = '';
      this.showPassword = false;
      this.showConfirmPassword = false;
    },

    togglePasswordVisibility() {
      this.showPassword = !this.showPassword;
    },

    toggleConfirmPasswordVisibility() {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-wrapper {
  width: 100%;
  max-width: 448px;
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.logo-icon {
  color: white;
}

.app-title {
  font-size: 30px;
  font-weight: bold;
  color: white;
  margin-bottom: 8px;
}

.app-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
}

.login-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.card-header {
  padding: 24px 24px 0;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1a202c;
}

.card-description {
  color: #718096;
  font-size: 14px;
}

.card-content {
  padding: 24px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #2d3748;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 12px;
  color: #a0aec0;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
  box-sizing: border-box;
}

.form-input.with-icon {
  padding-left: 40px;
}

.form-input.with-toggle {
  padding-right: 44px;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.toggle-password-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  padding: 6px;
  cursor: pointer;
  color: #a0aec0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.toggle-password-btn:hover {
  color: #667eea;
  background-color: rgba(102, 126, 234, 0.1);
}

.toggle-password-btn:active {
  transform: translateY(-50%) scale(0.95);
}

.toggle-password-btn svg {
  pointer-events: none;
}

.forgot-password {
  text-align: right;
}

.link-button {
  background: none;
  border: none;
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  text-decoration: underline;
}

.link-button:hover {
  color: #5568d3;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background-color: #fee;
  border: 1px solid #fcc;
  border-radius: 6px;
  color: #c33;
  font-size: 14px;
}

.error-icon {
  flex-shrink: 0;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-button:hover {
  background: #5568d3;
}

.submit-button:disabled {
  background: #a0aec0;
  cursor: not-allowed;
  opacity: 0.7;
}

.divider {
  margin: 24px 0;
  text-align: center;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  height: 1px;
  background: #e2e8f0;
}

.divider span {
  position: relative;
  background: white;
  padding: 0 16px;
  color: #718096;
  font-size: 14px;
}

.toggle-mode {
  text-align: center;
  font-size: 14px;
  color: #4a5568;
}

.link-button-primary {
  background: none;
  border: none;
  color: #667eea;
  font-weight: 500;
  cursor: pointer;
  margin-left: 4px;
  text-decoration: underline;
}

.link-button-primary:hover {
  color: #5568d3;
}
</style>