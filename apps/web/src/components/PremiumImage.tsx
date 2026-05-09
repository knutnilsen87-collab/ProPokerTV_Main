import { useState } from "react";

type PremiumImageProps = {
  src?: string | null;
  alt: string;
  className?: string;
  fallbackLabel?: string;
};

export function PremiumImage({ src, alt, className, fallbackLabel = "PPTV" }: PremiumImageProps) {
  const [failed, setFailed] = useState(false);

  if (!src || failed) {
    return (
      <div className={className ? `${className} premium-image-fallback` : "premium-image-fallback"} role="img" aria-label={alt}>
        <span>{fallbackLabel}</span>
      </div>
    );
  }

  return <img className={className} src={src} alt={alt} onError={() => setFailed(true)} />;
}
