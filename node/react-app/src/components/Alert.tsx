import { ReactNode } from "react";

interface Props {
  children: ReactNode;
  onCloseClick: () => void;
}

const Alert = ({ children, onCloseClick }: Props) => {
  return (
    <div className="alert alert-warning alert-dismissible fade show">
      {children}
      <button
        type="button"
        className="btn-close"
        data-bs-dismiss="alert"
        aria-label="Close"
        onClick={onCloseClick}
      />
    </div>
  );
};

export default Alert;
