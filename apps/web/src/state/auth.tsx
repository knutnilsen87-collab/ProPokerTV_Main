import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type PropsWithChildren,
} from "react";
import * as api from "../lib/api";
import type { AuthResponse, CurrentUser } from "../types";

type StoredTokens = { accessToken: string; refreshToken: string } | null;

type AuthContextValue = {
  currentUser: CurrentUser | null;
  tokens: StoredTokens;
  loading: boolean;
  authError: string | null;
  setTokens: (tokens: StoredTokens) => void;
  signIn: (email: string, password: string) => Promise<void>;
  signUp: (email: string, password: string) => Promise<void>;
  signOut: () => void;
  refreshCurrentUser: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);
const STORAGE_KEY = "pptv-auth";

function mapAuthToTokens(response: AuthResponse): StoredTokens {
  return {
    accessToken: response.tokens.accessToken,
    refreshToken: response.tokens.refreshToken,
  };
}

export function AuthProvider({ children }: PropsWithChildren) {
  const [tokens, setTokensState] = useState<StoredTokens>(() => {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as StoredTokens) : null;
  });
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [loading, setLoading] = useState(true);
  const [authError, setAuthError] = useState<string | null>(null);

  const setTokens = useCallback((next: StoredTokens) => {
    setTokensState(next);
    if (next) {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    } else {
      window.localStorage.removeItem(STORAGE_KEY);
      setCurrentUser(null);
    }
  }, []);

  const refreshCurrentUser = useCallback(async () => {
    if (!tokens) {
      setCurrentUser(null);
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const me = await api.withRefresh(api.getCurrentUser, tokens, setTokens);
      setCurrentUser(me);
      setAuthError(null);
    } catch (error) {
      setCurrentUser(null);
      setAuthError(error instanceof Error ? error.message : "Failed to restore session.");
    } finally {
      setLoading(false);
    }
  }, [tokens, setTokens]);

  useEffect(() => {
    void refreshCurrentUser();
  }, [refreshCurrentUser]);

  const signIn = useCallback(
    async (email: string, password: string) => {
      const response = await api.login(email, password);
      setTokens(mapAuthToTokens(response));
      const me = await api.getCurrentUser(mapAuthToTokens(response)!);
      setCurrentUser(me);
      setAuthError(null);
    },
    [setTokens],
  );

  const signUp = useCallback(
    async (email: string, password: string) => {
      const response = await api.signup(email, password);
      setTokens(mapAuthToTokens(response));
      const me = await api.getCurrentUser(mapAuthToTokens(response)!);
      setCurrentUser(me);
      setAuthError(null);
    },
    [setTokens],
  );

  const signOut = useCallback(() => {
    setTokens(null);
    setAuthError(null);
  }, [setTokens]);

  const value = useMemo<AuthContextValue>(
    () => ({
      currentUser,
      tokens,
      loading,
      authError,
      setTokens,
      signIn,
      signUp,
      signOut,
      refreshCurrentUser,
    }),
    [currentUser, tokens, loading, authError, setTokens, signIn, signUp, signOut, refreshCurrentUser],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
