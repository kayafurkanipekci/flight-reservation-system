import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface LayoutProps {
  children: React.ReactNode;
}

export default function Layout({ children }: LayoutProps) {
  const { email, role, logout } = useAuth();
  const location = useLocation();

  const navigation = [
    { name: 'Airports', href: '/' },
    { name: 'Airplanes', href: '/airplanes' },
    { name: 'Flights', href: '/flights' },
    { name: 'Reservations', href: '/reservations' },
  ];

  const isActive = (path: string) => {
    if (path === '/') {
      return location.pathname === '/' || location.pathname === '/airports';
    }
    return location.pathname === path;
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Navigation Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-40 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex items-center space-x-8">
              {/* Logo */}
              <Link to="/" className="flex items-center gap-2.5 text-xl font-black text-brand-500 tracking-tight">
                <img
                  src="/logo.png"
                  alt="ButterFlight Logo"
                  className="h-8 w-auto object-contain"
                  onError={(e) => {
                    e.currentTarget.style.display = 'none';
                  }}
                />
                <span>ButterFlight</span>
              </Link>

              {/* Navigation Links */}
              <nav className="hidden md:flex space-x-1">
                {navigation.map((item) => (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`px-3 py-2 rounded-md text-sm font-semibold transition-colors ${
                      isActive(item.href)
                        ? 'bg-brand-50 text-brand-700'
                        : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                    }`}
                  >
                    {item.name}
                  </Link>
                ))}
              </nav>
            </div>

            {/* User Profile and Logout */}
            <div className="flex items-center space-x-4">
              <div className="text-right hidden sm:block">
                <p className="text-sm font-semibold text-gray-900">{email}</p>
                {role === 'ADMIN' && (
                  <span className="inline-flex items-center rounded-full px-2 py-0.5 text-xs font-bold mt-0.5 bg-red-50 text-red-700 border border-red-100">
                    ADMIN
                  </span>
                )}
              </div>

              <button
                onClick={logout}
                className="px-3.5 py-1.5 border border-gray-300 rounded-md text-sm font-semibold text-gray-700 bg-white hover:bg-red-50 hover:text-red-600 hover:border-red-200 transition-colors"
              >
                Logout
              </button>
            </div>
          </div>
        </div>

        {/* Mobile Navigation Links */}
        <div className="md:hidden border-t border-gray-100 px-4 py-2 flex justify-around bg-gray-50">
          {navigation.map((item) => (
            <Link
              key={item.name}
              to={item.href}
              className={`px-2.5 py-1.5 rounded-md text-xs font-bold transition-colors ${
                isActive(item.href)
                  ? 'bg-brand-100 text-brand-700'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              {item.name}
            </Link>
          ))}
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  );
}
